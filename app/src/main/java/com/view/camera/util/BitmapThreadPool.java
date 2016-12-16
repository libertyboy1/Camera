package com.view.camera.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class BitmapThreadPool {
	private static BitmapThreadPool mInstance;
	private ThreadPoolExecutor mThreadPoolExec;
	private int MAX_POOL_SIZE;
	private static final int KEEP_ALIVE = 10;
	BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

	public static synchronized void post(Runnable runnable) {
		if (mInstance == null) {
			mInstance = new BitmapThreadPool();
		}
		mInstance.mThreadPoolExec.execute(runnable);
	}

	private BitmapThreadPool() {
		int coreNum = Runtime.getRuntime().availableProcessors();
		MAX_POOL_SIZE = coreNum * 2;
		mThreadPoolExec = new ThreadPoolExecutor(coreNum, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, workQueue);
	}

	public static void finish() {
		mInstance.mThreadPoolExec.shutdown();
	}
}
