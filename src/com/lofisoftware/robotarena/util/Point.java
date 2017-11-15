package com.lofisoftware.robotarena.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;

public class Point {
	private int x;
	private int y;

	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Point(float x2, float y2) {
		set(x2,y2);
	}

	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	public Point set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Point set(Point p) {
		this.x = p.x;
		this.y = p.y;
		return this;
	}
	
	public Point set(float f, float g) {
		this.x = MathUtils.round(f);
		this.y = MathUtils.round(g);
		return this;
		
	}
	
	public Point copy() {
		return new Point(x, y);
	}

	public int distanceTo(Point other){
		return Math.max(Math.abs(x-other.x), Math.abs(y-other.y)); 
	}

	public List<Point> neighbors4() {
		List<Point> neighbors = Arrays.asList(
									new Point(x+0,y-1),
				new Point(x-1,y+0),                     new Point(x+1,y+0),
									new Point(x+0,y+1));

		Collections.shuffle(neighbors);
		return neighbors;
	}
	
	public List<Point> neighbors8() {
		List<Point> neighbors = Arrays.asList(
				new Point(x-1,y-1), new Point(x+0,y-1), new Point(x+1,y-1), 
				new Point(x-1,y+0),                     new Point(x+1,y+0),
				new Point(x-1,y+1), new Point(x+0,y+1), new Point(x+1,y+1));

		Collections.shuffle(neighbors);
		return neighbors;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (Point.class.isAssignableFrom(obj.getClass()))
			return (this.equals((Point)obj));
		return super.equals(obj);
	}

	public boolean equals(Point p) {
		if (p.x == x & p.y == y)
			return true;
		return false;
	}

	public Point plus(int dx, int dy) {
		return new Point(x+dx, y+dy);
	}

	public Point plus(Point other){
		return new Point(x+other.x, y+other.y);
	}

	public Point minus(Point other){
		return new Point(x-other.x, y-other.y);
	}


}