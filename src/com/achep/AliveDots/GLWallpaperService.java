package com.achep.AliveDots;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService {

	public abstract class GLEngine extends Engine {
		class WallpaperGLSurfaceView extends GLSurfaceView {

			WallpaperGLSurfaceView(Context context) {
				super(context);
			}

			@Override
			public SurfaceHolder getHolder() {
				return getSurfaceHolder();
			}

			public void onDestroy() {
				super.onDetachedFromWindow();
			}
		}

		private WallpaperGLSurfaceView mGLSurfaceView;

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			mGLSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);

			if (visible) {
				mGLSurfaceView.onResume();
				onResume();
			} else {
				if (!isPreview()) {
					mGLSurfaceView.onPause();
					onPause();
				}
			}
		}

		public abstract void onResume();

		public abstract void onPause();

		@Override
		public void onDestroy() {
			super.onDestroy();
			mGLSurfaceView.onDestroy();
		}

		protected WallpaperGLSurfaceView getWallpaperGLSurfaceView() {
			return mGLSurfaceView;
		}
	}
}