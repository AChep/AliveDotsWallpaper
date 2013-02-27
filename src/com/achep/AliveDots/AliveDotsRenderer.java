/*
 * Copyright (C) 2013 AChep@xda <artemchep@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.achep.AliveDots;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.achep.AliveDots.gles20.GLES20Helper;
import com.achep.alivedots.R;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class AliveDotsRenderer implements Renderer {

	private float[] mProjMatrix = new float[16];
	private Context mContext;

	private int mConfigSpeed = 6000;
	private int mConfigDotsSize = 20;
	private int mConfigDividerSize = 0;

	private int mPointsNum;
	private int mPointsBufferHandler;

	private int mProgram;
	private int maPosition;
	private int maTimeShift;
	private int maHighColor;
	private int muPointSize;
	private int muTime;
	private int muColor;

	public AliveDotsRenderer(Context context) {
		super();
		mContext = context;
	}

	@Override
	public void onDrawFrame(GL10 arg0) {

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mPointsBufferHandler);
		GLES20.glEnableVertexAttribArray(maPosition);
		GLES20.glVertexAttribPointer(maPosition, 2, GLES20.GL_FLOAT, false, 16,
				0);
		GLES20.glEnableVertexAttribArray(maTimeShift);
		GLES20.glVertexAttribPointer(maTimeShift, 1, GLES20.GL_FLOAT, false,
				16, 8);
		GLES20.glEnableVertexAttribArray(maHighColor);
		GLES20.glVertexAttribPointer(maHighColor, 1, GLES20.GL_FLOAT, false,
				16, 12);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glUniform1f(muPointSize, mConfigDotsSize);
		//GLES20.glUniform4f(muColor, 0.4f, 0.803f, 1f, 1f);
		GLES20.glUniform4f(muColor, 0f, 1f, 0f, 1f);
		GLES20.glUniform1f(muTime, System.currentTimeMillis() % mConfigSpeed
				/ (float) mConfigSpeed);

		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, mPointsNum);

		GLES20.glDisableVertexAttribArray(maPosition);
		GLES20.glDisableVertexAttribArray(maTimeShift);
		GLES20.glDisableVertexAttribArray(maHighColor);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float ratio = (float) width / height;
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 1000);

		int shift = mConfigDotsSize / 2 + mConfigDividerSize;
		int step = mConfigDotsSize + mConfigDividerSize;

		mPointsNum = (int) (Math.floor((width + shift) / step) * Math
				.floor((height + shift) / step));

		float[] points = new float[mPointsNum * 4];

		int i = 0;
		for (int y = shift; y < height; y += step) {
			for (int x = shift; x < width; x += step) {
				points[i + 0] = x / (float) width * 2f - 1f;
				points[i + 1] = 1f - y / (float) height * 2f;
				points[i + 2] = (float) Math.random();
				points[i + 3] = (float) Math.pow(Math.random(), 1.8f);

				i += 4;
			}
		}
		if (mPointsBufferHandler != 0) {
			GLES20.glDeleteBuffers(1, new int[] { mPointsBufferHandler }, 0);
		}
		mPointsBufferHandler = GLES20Helper.loadBuffer(points);

		int vertexShaderHandle = GLES20Helper.compileShader(
				GLES20.GL_VERTEX_SHADER, GLES20Helper.readFromRawResource(
						mContext, R.raw.vertex_shader_code));
		int fragmentShaderHandle = GLES20Helper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, GLES20Helper.readFromRawResource(
						mContext, R.raw.fragment_shader_code));

		mProgram = GLES20Helper.createAndLinkProgram(vertexShaderHandle,
				fragmentShaderHandle, new String[] { "a_Position",
						"a_TimeShift", "a_HighColor" });

		maPosition = GLES20.glGetAttribLocation(mProgram, "a_Position");
		maTimeShift = GLES20.glGetAttribLocation(mProgram, "a_TimeShift");
		maHighColor = GLES20.glGetAttribLocation(mProgram, "a_HighColor");
		muPointSize = GLES20.glGetUniformLocation(mProgram, "u_PointSize");
		muTime = GLES20.glGetUniformLocation(mProgram, "u_Time");
		muColor = GLES20.glGetUniformLocation(mProgram, "u_Color");

		GLES20.glUseProgram(mProgram);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
	}
}