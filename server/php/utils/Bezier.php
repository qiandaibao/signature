<?php

/**
 * User: xrh
 * Date: 15-2-9 上午10:03
 * Description:
 */
class Bezier
{
    /**
     * @var $startPoint TimedPoint
     */
    public $startPoint;
    /**
     * @var $startPoint TimedPoint
     */
    public $control1;
    /**
     * @var $startPoint TimedPoint
     */
    public $control2;
    /**
     * @var $startPoint TimedPoint
     */
    public $endPoint;

    public function __construct($startPoint, $control1, $control2, $endPoint)
    {
        $this->startPoint = $startPoint;
        $this->control1 = $control1;
        $this->control2 = $control2;
        $this->endPoint = $endPoint;
    }

    public function length()
    {
        $steps = 20;
        $length = 0;
        $px = 0.0;
        $py = 0.0;

        for ($i = 0; $i <= $steps; ++$i) {
            $t = $i / $steps;
            $cx = $this->point($t, $this->startPoint->x, $this->control1->x,
                $this->control2->x, $this->endPoint->x);
            $cy = $this->point($t, $this->startPoint->y, $this->control1->y,
                $this->control2->y, $this->endPoint->y);
            if ($i > 0) {
                $xdiff = $cx - $px;
                $ydiff = $cy - $py;
                $length += sqrt($xdiff * $xdiff + $ydiff * $ydiff);
            }
            $px = $cx;
            $py = $cy;
        }
        return $length;
    }

    function point($t, $start, $c1, $c2, $end)
    {
        return $start * (1.0 - $t) * (1.0 - $t) * (1.0 - $t)
        + 3.0 * $c1 * (1.0 - $t) * (1.0 - $t) * $t
        + 3.0 * $c2 * (1.0 - $t) * $t * $t
        + $end * $t * $t * $t;
    }

}