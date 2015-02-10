<?php

/**
 * User: xrh
 * Date: 15-2-9 上午10:03
 * Description:
 */
class TimedPoint
{
    public $x;
    public $y;
    public $timestamp;

    function __construct($x, $y, $timestamp)
    {
        $this->x = $x;
        $this->y = $y;
        $this->timestamp = $timestamp;
    }

    /**
     * @param TimedPoint $start
     * @return float
     */
    function velocityFrom($start)
    {
        if ($this->timestamp - $start->timestamp > 0) {
            $velocity = $this->distanceTo($start) / ($this->timestamp - $start->timestamp);
        } else {
            $velocity = 0.0;
        }
        return $velocity;
    }

    /**
     * @param $point TimedPoint
     * @return float
     */
    function distanceTo($point)
    {
        return sqrt(pow($point->x - $this->x, 2), pow($point->y - $this->y, 2));
    }
}