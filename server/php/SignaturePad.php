<?php
include_once dirname(__FILE__) . "/utils/TimedPoint.php";
include_once dirname(__FILE__) . "/utils/ControlTimedPoints.php";
include_once dirname(__FILE__) . "/utils/Bezier.php";

/**
 * User: xrh
 * Date: 15-2-9 上午11:03
 * Description:
 */
class SignaturePad
{
    /**
     * @var $mPoints TimedPoint[]
     */
    private $mPoints;
    private $mLastVelocity;
    private $mLastWidth;

    //Configurable parameters
    private $mMinWidth;
    private $mMaxWidth;
    private $mVelocityFilterWeight;

    public $mSignatureBitmap;

    function __construct()
    {
        //Configurable parameters
        $this->mMinWidth = 3.0; //3.0
        $this->mMaxWidth = 13.0; //7.0
        $this->mVelocityFilterWeight = 0.9;

        $this->mSignatureBitmap = imagecreatetruecolor(1000, 1000);

        $this->clear();
    }

    /**
     * @param TimedPoint $newPoint
     */
    function addPoint($newPoint)
    {
        $this->mPoints[] = $newPoint;
        if (sizeof($this->mPoints) > 2) {
            // To reduce the initial lag make it work with 3 mPoints
            // by copying the first point to the beginning.

            if (sizeof($this->mPoints) == 3) {
                array_unshift($this->mPoints, $this->mPoints[0]);
            }

            $tmp = $this->calculateCurveControlPoints($this->mPoints[0], $this->mPoints[1], $this->mPoints[2]);
            $c2 = $tmp->c2;
            $tmp = $this->calculateCurveControlPoints($this->mPoints[1], $this->mPoints[2], $this->mPoints[3]);
            $c3 = $tmp->c1;
            $curve = new Bezier($this->mPoints[1], $c2, $c3, $this->mPoints[2]);

            $startPoint = $curve->startPoint;
            $endPoint = $curve->endPoint;

            $velocity = $endPoint->velocityFrom($startPoint);

            $velocity = $this->mVelocityFilterWeight * $velocity
                + (1 - $this->mVelocityFilterWeight) * $this->mLastVelocity;

            // The new width is a function of the velocity. Higher velocities
            // correspond to thinner strokes.
            $newWidth = $this->strokeWidth($velocity);

            // The Bezier's width starts out as last curve's final width, and
            // gradually changes to the stroke width just calculated. The new
            // width calculation is based on the velocity between the Bezier's
            // start and end mPoints.
            $this->addBezier($curve, $this->mLastWidth, $newWidth);

            $this->mLastVelocity = $velocity;
            $this->mLastWidth = $newWidth;

            // Remove the first element from the list,
            // so that we always have no more than 4 mPoints in mPoints array.
            array_shift($this->mPoints);
        }
    }

    /**
     * @param Bezier $curve
     * @param $startWidth
     * @param $endWidth
     */
    function addBezier($curve, $startWidth, $endWidth)
    {
        $widthDelta = $endWidth - $startWidth;
        $drawSteps = floor($curve->length());

        //var_dump($drawSteps);

        for ($i = 0; $i < $drawSteps; $i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            $t = ((float)$i) / $drawSteps;
            $tt = $t * $t;
            $ttt = $tt * $t;
            $u = 1 - $t;
            $uu = $u * $u;
            $uuu = $uu * $u;

            $x = $uuu * $curve->startPoint->x;
            $x += 3 * $uu * $t * $curve->control1->x;
            $x += 3 * $u * $tt * $curve->control2->x;
            $x += $ttt * $curve->endPoint->x;

            $y = $uuu * $curve->startPoint->y;
            $y += 3 * $uu * $t * $curve->control1->y;
            $y += 3 * $u * $tt * $curve->control2->y;
            $y += $ttt * $curve->endPoint->y;

            // Set the incremental stroke width and draw.
            $color = imagecolorallocate($this->mSignatureBitmap, 255, 0, 0);
            $strokeWidth = $startWidth + $ttt * $widthDelta;

            imagefilledellipse($this->mSignatureBitmap, $x, $y, $strokeWidth / 2.0, $strokeWidth / 2.0, $color);
        }
    }

    /**
     * @param TimedPoint $s1
     * @param TimedPoint $s2
     * @param TimedPoint $s3
     * @return ControlTimedPoints
     */
    function calculateCurveControlPoints($s1, $s2, $s3)
    {
        $dx1 = $s1->x - $s2->x;
        $dy1 = $s1->y - $s2->y;
        $dx2 = $s2->x - $s3->x;
        $dy2 = $s2->y - $s3->y;

        $m1 = new TimedPoint(($s1->x + $s2->x) / 2.0, ($s1->y + $s2->y) / 2.0, 0);
        $m2 = new TimedPoint(($s2->x + $s3->x) / 2.0, ($s2->y + $s3->y) / 2.0, 0);

        $l1 = sqrt($dx1 * $dx1 + $dy1 * $dy1);
        $l2 = sqrt($dx2 * $dx2 + $dy2 * $dy2);

        $dxm = ($m1->x - $m2->x);
        $dym = ($m1->y - $m2->y);

        if ($l1 + $l2 == 0) {
//            var_dump("----------->");
//            var_dump($s1, $s2, $s3);
//            var_dump($l1, $l2);
//            var_dump($dx1, $dy1, $dx2, $dy2);
//            exit();
            $k = 0;
        } else {
            $k = $l2 / ($l1 + $l2);
        }


        $cm = new TimedPoint($m2->x + $dxm * $k, $m2->y + $dym * $k, 0);

        $tx = $s2->x - $cm->x;
        $ty = $s2->y - $cm->y;

        return new ControlTimedPoints(new TimedPoint($m1->x + $tx, $m1->y + $ty, 0), new TimedPoint($m2->x + $tx, $m2->y + $ty, 0));
    }

    function  strokeWidth($velocity)
    {
        return max($this->mMaxWidth / ($velocity + 1), $this->mMinWidth);
    }

    function clear()
    {
        $this->mPoints = array();
        $this->mLastVelocity = 0.0;
        $this->mLastWidth = ($this->mMinWidth + $this->mMaxWidth) / 2.0;
    }
} 