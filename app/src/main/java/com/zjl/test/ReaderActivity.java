//#if ${enable.tbs_reader}
package com.zjl.test;

import java.io.File;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.TbsReaderView.ReaderCallback;
import com.tencent.smtt.utils.TbsLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReaderActivity extends Activity implements ReaderCallback
{
	private static final String	TAG				= "ReaderActivity";
	private String				mFilePath;
	private TbsReaderView		mTbsReaderView;

	ReaderTopBar				mTopBar;
	ReaderToolBar				mToolBar;

	FrameLayout					mRootView;
	int							mBarHeight;

	private boolean				mIsHidden		= false;
	private boolean				mIsAnimating	= false;
	
	//该值必须与定义在浏览器 ReaderMenuController里的 READER_REQ_FEATURE_KEY 一致
	//public static final String READER_REQ_FEATURE_KEY	= "REQ_FEATURE_ID";
	private String				KEY_FEATRUE_ID	= "REQ_FEATURE_ID";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.reader_layout);
		mRootView = (FrameLayout) findViewById(R.id.content_layout);
		handleOpenFile(this.getIntent());
		boolean ret = TbsReaderView.isSupportExt(this,"txt");
		TbsReaderView.getResDrawable(this, READER_PLUGIN_RES_FIXSCREEN_NORMAL);
//		initBar();
		
	}

	Bundle mFileBundle = null ;
	private boolean handleOpenFile(Intent intent)
	{
		if (null == intent)
			return false;

		mFilePath = intent.getStringExtra("filePath");
		if (mFilePath == null) {
			Uri uri = intent.getData();
			mFilePath = uri.getPath();
		}
		Log.d("zjltest", "mFilePath:" + mFilePath);

		//Toast.makeText(this, "File Path:" + mFilePath, Toast.LENGTH_LONG).show();
		Bundle param = new Bundle();
		param.putString(TbsReaderView.KEY_FILE_PATH, mFilePath);
		param.putString(TbsReaderView.KEY_TEMP_PATH, Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");
		param.putBoolean("menu_show",false);
		mFileBundle = param ;
		boolean checkPlugin = false ;
		if (null == mTbsReaderView)
		{
			mTbsReaderView = new TbsReaderView(this, this);
		}
		String ext = mFilePath.substring(mFilePath.lastIndexOf(".") + 1);
		boolean ret = mTbsReaderView.preOpen(ext, true);
		Log.d("zjltest", "preOpen:" + ret);
		mTbsReaderView.openFile(param);//mTbsReaderView.preOpen(getFileExt(mFilePath), checkPlugin) ;
//		if (!checkPlugin && ret )
//		{
//			mTbsReaderView.openFile(param);
//		}

		mRootView.addView(mTbsReaderView, new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		

		return true;
	}

	public static String getFileExt(String fileName)
	{
		if (TextUtils.isEmpty(fileName))
			return null;

		String fileExt = null;
		int idx = fileName.lastIndexOf('.');
		if (idx > -1 && idx < fileName.length() - 1)
		{
			fileExt = fileName.substring(idx + 1);
			if (fileExt.indexOf(File.separatorChar) > -1)
				return null;
		}

		return fileExt;
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		handleOpenFile(intent);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		if (null != mTbsReaderView)
		{
			mTbsReaderView.onStop();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		//		mReaderWrapper.unload();
		//		mReaderWrapper = null;
		super.onDestroy();
	}

	private void initBar()
	{
		mBarHeight = dip2px(48);
		
		mTopBar = new ReaderTopBar(this);
		FrameLayout.LayoutParams topBarFrameLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mBarHeight);
		mRootView.addView(mTopBar, topBarFrameLayout);

		mToolBar = new ReaderToolBar(this);
		FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mBarHeight);
		parms.gravity = Gravity.BOTTOM;
		mRootView.addView(mToolBar, parms);
	}

	private void onMenuClick()
	{
		String ext =getFileExt(mFilePath);
		if (ext.equalsIgnoreCase("pdf"))
		{
			mTbsReaderView.doCommand(ReaderCallback.READER_PLUGIN_COMMAND_PDF_LIST, null, null);
		}
		else if (ext.equalsIgnoreCase("pptx") || ext.equalsIgnoreCase("ppt"))
		{
			mTbsReaderView.doCommand(ReaderCallback.READER_PLUGIN_COMMAND_PPT_PLAYER, null, null);
		}else if(ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docx"))
		{
			String text ="k" ;
			mTbsReaderView.doCommand(ReaderCallback.READER_PLUGIN_COMMAND_TEXT_FIND, text, null);
		}
			
		
		//mTbsReaderView.doCommand(new Integer(5015), null, null);
//		Intent i = new Intent("com.tencent.QQBrowser.action.VIEW");
//		i.setData(Uri.parse("http://m.sogou.com/web/searchList.jsp?pid=sogou-clse-2996962656838a97&e=1427&g_f=123&keyword=" + "小猫"));
//		startActivity(i);
	}
	
	@Override
	public void onCallBackAction(Integer actionType, Object args, Object result)
	{
		Log.d(TAG, "onCallBackAction actionType:" + actionType + " args" + args + " result" + result);
		switch (actionType)
		{
			case ReaderCallback.NOTIFY_CANDISPLAY:
			case ReaderCallback.NOTIFY_ERRORCODE:
			{
				//mTbsReaderView.onSizeChanged(600, 800);
				if(args instanceof Bundle)
				{
					Bundle more = (Bundle) args ;
					boolean menu = more.getBoolean("more_menu");
					Log.d(TAG, "onCallBackAction menu=" + menu);
				}
				
				break;
			}
			case ReaderCallback.GET_BAR_ANIMATING:
			{
				Bundle bundle;
				if (result instanceof Bundle)
				{
					bundle = (Bundle) result;
				}
				else 
				{
					bundle = new Bundle();
				}
				bundle.putBoolean(TbsReaderView.IS_BAR_ANIMATING, mIsAnimating);
				break;
			}
			case ReaderCallback.HIDDEN_BAR:
			{
				setBarHidden(true);
				break;
			}
			case ReaderCallback.SHOW_BAR:
			{
				setBarHidden(false);
				break;
			}
			case ReaderCallback.READER_PDF_LIST:
			{
				Bundle extrals = new Bundle() ;

				//Feature ID 定义在浏览器 ReaderMenuController
				//public static final int	READER_REQ_FEATURE_PDF_OUTLINE 		= 4011;
				extrals.putInt(KEY_FEATRUE_ID, 4011);
			
				 
				//String path = (String) args ;
				String path = ((Bundle)args).getString("docpath");
				extrals.putParcelableArray("outlinedata", ((Bundle)args).getParcelableArray("outlinedata"));
				QbSdk.startQBForDoc(this, path, 4, 0, "pdf", extrals);
				break;
			}
			case ReaderCallback.READER_PPT_PLAY_MODEL:
			{
				Bundle extrals = new Bundle() ;
				
				//Feature ID 定义在浏览器 ReaderMenuController
				//public static final int	READER_REQ_FEATURE_PPT_PLAY_MODEL 	= 4012;
				extrals.putInt(KEY_FEATRUE_ID, 4012);
			
				String path = (String) args ;
				QbSdk.startQBForDoc(this, path, 4, 0, "ppt", extrals);
				break;
			}
			case ReaderCallback.READER_TXT_READING_MODEL:
			{
				Bundle extrals = new Bundle() ;

				//Feature ID 定义在浏览器 ReaderMenuController
				//public static final int	READER_REQ_FEATURE_TXT_READING_MODEL = 4013;
				extrals.putInt(KEY_FEATRUE_ID, 4013);
			
				String path = (String) args ;
				QbSdk.startQBForDoc(this, path, 4, 0, "txt", extrals);
				break;
			}
			case READER_PLUGIN_STATUS:
			{
				int err = (Integer) args;
				if (err == 0 )
				{
					mTbsReaderView.openFile(mFileBundle);
				}
				break;
			}
			default:
				break;
		}
	}
	
	public int dip2px(int dip)
	{
		float scale = this.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	class ReaderTopBar extends LinearLayout
	{
		private TextView	mBackBtn;
		private TextView	mTitle;
		private TextView	mFunBtn;

		public ReaderTopBar(Context context)
		{
			super(context);
			setOrientation(LinearLayout.HORIZONTAL);
			setBackgroundColor(Color.GRAY);
			//setAlpha(0.7f);
			setGravity(Gravity.CENTER_VERTICAL);


			mBackBtn = new TextView(context);
			mBackBtn.setText("返 回");
			mBackBtn.setTextSize(18);
			mBackBtn.setTextColor(Color.BLACK);
			mBackBtn.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
			addView(mBackBtn);
			mBackBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					finish();
				}
			});


			mTitle = new TextView(context);
			mTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
			mTitle.setTextColor(Color.BLACK);
			mTitle.setTextSize(22);
			mTitle.setGravity(Gravity.CENTER);
			addView(mTitle);

			mFunBtn = new TextView(context);
			mFunBtn.setGravity(Gravity.RIGHT);
			mFunBtn.setTextSize(18);
			mFunBtn.setTextColor(Color.BLACK);
			mFunBtn.setText("功 能");
			mFunBtn.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
			addView(mFunBtn);
			mFunBtn.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					onMenuClick();
				}
			});
			
			setTitle("文件名");
		}

		public void setTitle(String title)
		{
			mTitle.setText(title);
		}

		boolean	mIsAnimating	= false;

		public boolean isAnimating()
		{
			return mIsAnimating;
		}
	}
	
	private void setBarHidden(boolean hidden)
	{
		
		if (mIsAnimating)
			return;
		
		Log.d(TAG, "switchBar: mIsHidden:" + mIsHidden);
		
		mIsAnimating = true;
		
		float topStartY = .0f;
		float topFinishY = .0f;
		float toolStartY = .0f;
		float toolFinishY = .0f;
		if (!hidden)
		{
			topStartY = -mBarHeight;
			topFinishY = .0f;
			
			toolStartY = mBarHeight;
			toolFinishY = .0f;
		}
		else
		{
			topStartY = .0f;
			topFinishY = -mBarHeight;
			
			toolStartY = .0f;
			toolFinishY = mBarHeight;
		}

		mIsAnimating = true;
		TranslateAnimation topBarAnimation = new TranslateAnimation(0, 0, topStartY, topFinishY);
		topBarAnimation.setDuration(200);
		topBarAnimation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				if (mIsHidden)
				{
					mTopBar.setVisibility(View.INVISIBLE);
				}

				mIsAnimating = false;
			}
		});
		
		TranslateAnimation toolBarAnimation = new TranslateAnimation(0, 0, toolStartY, toolFinishY);
		toolBarAnimation.setDuration(200);
		toolBarAnimation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				if (mIsHidden)
				{
					mToolBar.setVisibility(View.INVISIBLE);
				}

				mIsAnimating = false;
			}
		});
		
		if (!hidden)
		{
			mTopBar.setVisibility(View.VISIBLE);
			mToolBar.setVisibility(View.VISIBLE);
		}
		
		mIsHidden = hidden;
		mTopBar.startAnimation(topBarAnimation);
		mToolBar.startAnimation(toolBarAnimation);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if(1 == requestCode)
			{
				int pageNum = 5;
				Bundle params = new Bundle();
				params.putInt("progress", pageNum);
				mTbsReaderView.doCommand(new Integer(5021), params, null);
			}
		} 
		else if (resultCode == RESULT_CANCELED) {

		}

	}


	class ReaderToolBar extends LinearLayout
	{
		public ReaderToolBar(Context context)
		{
			super(context);

			setBackgroundColor(Color.GRAY);
	//		setAlpha(0.7f);
		}
	}
	
	
}
//#endif
