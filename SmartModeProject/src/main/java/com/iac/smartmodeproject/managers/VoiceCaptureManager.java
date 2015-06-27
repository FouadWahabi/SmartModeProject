package com.iac.smartmodeproject.managers;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Handler;
import android.util.Log;

import com.iac.smartmodeproject.listeners.INoiseLevelListener;

public class VoiceCaptureManager {

	private String TAG = "AudioCaptureManager";

	private static VoiceCaptureManager INSATANCE = new VoiceCaptureManager();
	private int bufferSize = 800;
	private short[] buffer = new short[bufferSize];
	private int average;
	private boolean recorderStarted = false;
	private AudioRecord recorder;
	private Handler mainHandler;
	private Context context;

	private VoiceCaptureManager() {

	}

	public void init(Context context) {
		this.context = context;
		if (recorder == null) {
			int minBufferSize = AudioRecord
					.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
			recorder = new AudioRecord(AudioSource.MIC, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 10);

		} else if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
			int minBufferSize = AudioRecord
					.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
			recorder = new AudioRecord(AudioSource.MIC, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 10);
		}
	}

	public void startRecording() {
		if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED)
			init(context);
		if (!recorderStarted) {
			recorder.startRecording();
			recorderStarted = true;
		}
	}

	public void stopRecording() {
		if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
				&& recorderStarted) {
			recorder.stop();
			recorderStarted = false;
		}
	}

	@Deprecated
	// get noise level at the current instant
	public int getNoiseLevel() {
		int max = 0;
		if (recorderStarted) {
			recorder.read(buffer, 0, bufferSize);
			for (int i = 0; i < bufferSize; i++) {
				if (Math.abs(buffer[i]) > max) {
					max = Math.abs(buffer[i]);
				}
			}
		}
		return (max - 1100) > 0 ? (max - 1100) : 0;
	}

	// noise level listener
	public synchronized void setNoiseLevelListener(final long delay,
			final INoiseLevelListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (recorderStarted) {
					int maxValue = 0;
					for (long stop = System.currentTimeMillis() + delay; stop > System
							.currentTimeMillis();) {
						int max = 0;
						recorder.read(buffer, 0, bufferSize);
						for (int i = 0; i < bufferSize; i++) {
							if (Math.abs(buffer[i]) > max) {
								max = Math.abs(buffer[i]);
							}
						}
						if (max > maxValue) {
							maxValue = max;
						}
					}
					Log.i(TAG, "setNoiseLevel : noise level is : " + maxValue);
					if (mainHandler == null) {
						mainHandler = new Handler(context.getMainLooper());
					}
					final int finalValue = (maxValue - 1100) > 0 ? (maxValue - 1100)
							: 0;
					mainHandler.post(new Runnable() {

						@Override
						public void run() {
							listener.noiseLevelReceived(finalValue);
						}
					});
					recorder.stop();
					recorderStarted = false;
					Thread.currentThread().interrupt();
				}
			}
		}).start();
	}

	// get singleton instance
	public static VoiceCaptureManager getInsatnce() {
		return INSATANCE;
	}
}
