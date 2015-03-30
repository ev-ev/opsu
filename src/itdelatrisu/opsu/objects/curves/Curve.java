/*
 * opsu! - an open-source osu! client
 * Copyright (C) 2014, 2015 Jeffrey Han
 *
 * opsu! is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opsu! is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opsu!.  If not, see <http://www.gnu.org/licenses/>.
 */

package itdelatrisu.opsu.objects.curves;

import itdelatrisu.opsu.render.CurveRenderState;
import itdelatrisu.opsu.GameImage;
import itdelatrisu.opsu.Utils;
import itdelatrisu.opsu.beatmap.HitObject;
import itdelatrisu.opsu.Options;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

/**
 * Representation of a curve.
 *
 * @author fluddokt (https://github.com/fluddokt)
 */
public abstract class Curve {
	/** Points generated along the curve should be spaced this far apart. */
	protected static float CURVE_POINTS_SEPERATION = 5;

	/** The associated HitObject. */
	protected HitObject hitObject;

	/** The scaled starting x, y coordinates. */
	protected float x, y;

	/** The scaled slider x, y coordinate lists. */
	protected float[] sliderX, sliderY;
		
	/** scaling factor for drawing. */
	protected static float scale;

	/** Per-curve render-state used for the new style curve renders*/
	private CurveRenderState renderState;

	/** Points along the curve (set by inherited classes). */
	protected Vec2f[] curve;

	/**
	 * Constructor.
	 * @param hitObject the associated HitObject
	 * @param color the color of this curve
	 */
	protected Curve(HitObject hitObject, Color color) {
		this.hitObject = hitObject;
		this.x = hitObject.getScaledX();
		this.y = hitObject.getScaledY();
		this.sliderX = hitObject.getScaledSliderX();
		this.sliderY = hitObject.getScaledSliderY();
		this.scale = 100;
		this.renderState = null;
	}

	/**
	 * Returns the point on the curve at a value t.
	 * @param t the t value [0, 1]
	 * @return the point [x, y]
	 */
	public abstract float[] pointAt(float t);

	/**
	 * Draws the full curve to the graphics context.
	 * @param color the color filter
	 */
	public void draw(Color color) {
			if ( curve == null){
				Log.error("draw curve"+this);
				return;
			}
			if (Options.GameOption.NEW_SLIDER.getBooleanValue()) {
				if(renderState == null)
				{
					renderState = new CurveRenderState(scale,hitObject);
				}
				renderState.draw(color,curve);
			} else {
				Image hitCircle = GameImage.HITCIRCLE.getImage();
				Image hitCircleOverlay = GameImage.HITCIRCLE_OVERLAY.getImage();
				for (int i = 0; i < curve.length; i++) {
					hitCircleOverlay.drawCentered(curve[i].x, curve[i].y, Utils.COLOR_WHITE_FADE);
				}
				for (int i = 0; i < curve.length; i++){
					hitCircle.drawCentered(curve[i].x, curve[i].y, color);
				}
			}
	}

	/**
	 * Returns the angle of the first control point.
	 */
	public abstract float getEndAngle();

	/**
	 * Returns the angle of the last control point.
	 */
	public abstract float getStartAngle();

	/**
	 * Returns the scaled x coordinate of the control point at index i.
	 * @param i the control point index
	 */
	public float getX(int i) { return (i == 0) ? x : sliderX[i - 1]; }

	/**
	 * Returns the scaled y coordinate of the control point at index i.
	 * @param i the control point index
	 */
	public float getY(int i) { return (i == 0) ? y : sliderY[i - 1]; }

	/**
	 * Set the scaling factor.
	 * @param factor the new scaling factor for the UI representation
	 */
	public static void setScale(float factor) {
			scale = factor;
	}

	/**
	 * Linear interpolation of a and b at t.
	 */
	protected float lerp(float a, float b, float t) {
		return a * (1 - t) + b * t;
	}

	public void discardCache() {
		if(renderState != null)
			renderState.discardCache();
	}
}
