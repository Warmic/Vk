package com.example.peter.vk1;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;


import android.app.Service;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;


/**
 * Main activity for chat window
 * 
 * @author Chirag Jain
 */
public class MainActivity extends FragmentActivity implements EmoticonsGridAdapter.KeyClickListener, ThumbnailsGridAdapter.KeyClickListener {
	
	private static final int NO_OF_EMOTICONS = 24;
	private static final int NO_OF_THUMBNAILS = 2;

	private ListView chatList;
	private View popUpView;
	private View thumbnails_popUpView;
	private View post_line;
	private View story_line;
	private ArrayList<Spanned> chats;
	private ChatListAdapter mAdapter;

	private Button story;
	private Button post;
	private ImageButton action_sticker;
	private ImageButton action_font;
	private Button postButton;


	private LinearLayout emoticonsCover;

	private LinearLayout parentLayout;
	private LinearLayout thumbnail_parent;

	private PopupWindow popupWindow;
	private LinearLayout footer;

	final static int lightBlue = 0xFF89A8CC;
	final static int darkBlue = 0xFF528BCC;
	private InputMethodManager imm;
	private float popUpheight;

	private boolean isKeyBoardVisible;
	
	private Bitmap[] emoticons;
	private Bitmap[] thumbnails;



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_main);

		getActionBar().hide();



		imm = (InputMethodManager)getBaseContext().getSystemService(Service.INPUT_METHOD_SERVICE);
		story_line =  findViewById(R.id.line_story);
		post_line =  findViewById(R.id.line_post);
		action_font = (ImageButton) findViewById(R.id.action_font);
		action_sticker = (ImageButton) findViewById(R.id.action_sticker);
		postButton = (Button) findViewById(R.id.post_button);

		chatList = (ListView) findViewById(R.id.chat_list);

		parentLayout = (LinearLayout) findViewById(R.id.list_parent);
		thumbnail_parent = (LinearLayout)  findViewById(R.id.thumb_parent);

		footer = (LinearLayout) findViewById(R.id.footer_layout);
		//thumbnails_footer = (LinearLayout) findViewById(R.id.thumbnails_layout);


		emoticonsCover = (LinearLayout) findViewById(R.id.footer_for_emoticons);
		//thumbnailsCover = (LinearLayout) findViewById(R.id.footer_for_thumbnails);


		popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup,parentLayout,false);

		thumbnails_popUpView = getLayoutInflater().inflate(R.layout.thumbnails_popup,thumbnail_parent);


		story = (Button) findViewById(R.id.story_button);
		post = (Button) findViewById(R.id.post_button);



		story.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(story_line.getVisibility()==View.INVISIBLE) {
					story_line.setVisibility(View.VISIBLE);
					post_line.setVisibility(View.INVISIBLE);

					story.setTextColor(darkBlue);
					post.setTextColor(lightBlue);

				}
			}
		});

		post.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(post_line.getVisibility()==View.INVISIBLE) {

					post_line.setVisibility(View.VISIBLE);
					story_line.setVisibility(View.INVISIBLE);

					post.setTextColor(darkBlue);
					story.setTextColor(lightBlue);
				}
				return false;
			}
		});


		// Setting adapter for chat list
		chats = new ArrayList<Spanned>();
		mAdapter = new ChatListAdapter(getApplicationContext(), chats);

		chatList.setAdapter(mAdapter);
		chatList.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow.isShowing())
					popupWindow.dismiss();

				footer.setVisibility(View.VISIBLE);

				return false;
			}
		});

		// Defining default height of keyboard which is equal to 230 dip
		popUpheight = (float) (getResources().getDimension(
				 R.dimen.keyboard_height) *  1.5);
		
		// Showing and Dismissing pop up on clicking emoticons button

		action_sticker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!popupWindow.isShowing() ) {

					footer.setVisibility(View.GONE);

					imm.hideSoftInputFromWindow(postButton.getWindowToken(), 0);

					isKeyBoardVisible = false;

					popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);

					emoticonsCover.setVisibility(LinearLayout.VISIBLE);


				}
			}
		});

		action_font.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!isKeyBoardVisible) {


					popupWindow.dismiss();

					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

					isKeyBoardVisible = true;

					footer.setVisibility(View.VISIBLE);


				}
			}
		});

		readEmoticons();
		readThumbnails();

		enablePopUpView();




		super.onCreate(savedInstanceState);



		// enableFooterView();


	}
	
	/**
	 * Reading all emoticons in local cache
	 */

	private void readThumbnails(){
		thumbnails = new Bitmap[NO_OF_THUMBNAILS];
		for (short i = 0; i < NO_OF_THUMBNAILS; i++) {
			thumbnails[i] = getImage("thumbnails/",(i+1)+ ".png");
		}

		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

	}

	private void readEmoticons () {
		
		emoticons = new Bitmap[NO_OF_EMOTICONS];
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {			
			emoticons[i] = getImage("emoticons/",(i+1) + ".png");
		}
		
	}

	/**
	 * Enabling all content in footer i.e. post window
	 */
/*
	private void enableFooterView() {


		content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				
			}
		});


		postButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (content.getText().toString().length() > 0) {

					/*
					Spanned sp = content.getText();					
					chats.add(sp);
					content.setText("");					
					mAdapter.notifyDataSetChanged();

				}

			}
		});
				/*
	}

	/**
	 * Overriding onKeyDown for dismissing keyboard on key down
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//TODO:FIX ONKEYDOWN
		footer.setVisibility(View.VISIBLE);
		isKeyBoardVisible = false;
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}




	/**
	 * Defining all components of emoticons keyboard
	 */
	private void enablePopUpView() {

		ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
		ViewPager thumbnails_pager = (ViewPager) thumbnails_popUpView.findViewById(R.id.thumbnails_pager);

		thumbnails_pager.setOffscreenPageLimit(3);
		pager.setOffscreenPageLimit(3);

		ArrayList<String> paths = new ArrayList<String>();
		ArrayList<String> thumbnails_paths = new ArrayList<String>();

		for (short i = 1; i <= NO_OF_EMOTICONS; i++) {
			paths.add(i + ".png");
		}

		for (short i = 1; i <= NO_OF_THUMBNAILS ; i++) {
			thumbnails_paths.add(i+".png");
		}

		EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(MainActivity.this, paths, this);
		ThumbnailsPagerAdapter thumbnails_adapter = new ThumbnailsPagerAdapter(MainActivity.this,thumbnails_paths,this);

		pager.setAdapter(adapter);
		thumbnails_pager.setAdapter(thumbnails_adapter);

		// Creating a pop window for emoticons keyboard
		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
				(int) popUpheight, false);


		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				emoticonsCover.setVisibility(LinearLayout.GONE);
			}
		});

	}

	/**
	 * For loading smileys from assets
	 */

	private Bitmap getImage(String path,String n) {
		AssetManager mngr = getAssets();
		Bitmap temp = null;
		InputStream in;
		try {
			in = mngr.open(path + n);
			temp = BitmapFactory.decodeStream(in, null, null);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return temp;
	}





	@Override
	public void keyClickedIndex(final String index) {
		
		ImageGetter imageGetter = new ImageGetter() {
            public Drawable getDrawable(String source) {    
            	StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(getResources(),emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };
        
        Spanned cs = Html.fromHtml("<img src ='"+ index +"'/>", imageGetter, null);        
		
		//int cursorPosition = content.getSelectionStart();
        //content.getText().insert(cursorPosition, cs);
        
	}

}
