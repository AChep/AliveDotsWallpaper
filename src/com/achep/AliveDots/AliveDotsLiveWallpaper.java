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

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.SurfaceHolder;

public class AliveDotsLiveWallpaper extends GLWallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new OpenGLES2Engine();
	}

	class OpenGLES2Engine extends GLWallpaperService.GLEngine {

		private Handler mHandler = new Handler();
		private Runnable mRunnable = new Runnable() {

			private static final int DELAY = 16;

			@Override
			public void run() {
				mGLSurfaceView.requestRender();
				mHandler.postDelayed(this, DELAY);
			}
		};

		private AliveDotsRenderer mNexusDotsRenderer;
		private WallpaperGLSurfaceView mGLSurfaceView;

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			// Setup renderer
			mNexusDotsRenderer = new AliveDotsRenderer(
					AliveDotsLiveWallpaper.this);
			mGLSurfaceView = getWallpaperGLSurfaceView();

			// OpenGL ES 2.0
			mGLSurfaceView.setEGLContextClientVersion(2);
			mGLSurfaceView.setRenderer(mNexusDotsRenderer);
			mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		}

		@Override
		public void onResume() {
			mHandler.post(mRunnable);
		}

		@Override
		public void onPause() {
			mHandler.removeCallbacks(mRunnable);
		}

		@Override
		public void onDestroy() {
		}
	}

}