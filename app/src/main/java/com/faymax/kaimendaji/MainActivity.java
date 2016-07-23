package com.faymax.kaimendaji;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Play 按键事件
    private ImageButton mBtnStart;

    // 文字框容器
    private List<WordButton> mAllWords;

    private List<WordButton> mSelectWords;

    private GridView mGridView;
    private static final int WORD_COUNT = 24;

    // 已选择文字框UI容器
    private LinearLayout mViewWordsContainer;

    private MyGridAdapter mAdapter;

    private Song mCurrentSong;

    private int mCurrentIndex;

    public final static String TAG = "MainActivity";

    private static final int STATUS_RIGHT = 1;
    private static final int STATUS_WRONG = 2;
    private static final int STATUS_LACK = 3;
    private static final int SPASH_TIMES = 6;

    private int mCurrentCoins;

    private TextView mViewCurrentCoins;
    private ImageButton mBtnDel;
    private ImageButton mBtnTip;

    private ImageButton mBtnBack;

    private int delWordCoins;
    private int tipWordCoins;

    private View mLayPass;

    private TextView mCurrentStagePass;
    private TextView mCurrentSongNamePass;

    private TextView mCurrentStage;

    private IAlertDialogButtonListener mDialogDelListener;
    private IAlertDialogButtonListener mDialogTipListener;
    private IAlertDialogButtonListener mDialogLackListener;
    private IAlertDialogButtonListener mDialogBackListener;

    public static final int ID_DIALOG_DEL = -1;
    public static final int ID_DIALOG_TIP = 0;
    public static final int ID_DIALOG_LACK = 1;
    public static final int ID_DIALOG_BACK = 2;

    private View mPreview;
    private TextView mTvTime;
    private int time = 3;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        initView();

        // 初始化动画

        initEvent();

        // 初始化游戏数据
        initData();

        refreshData();

        Countdown();
    }

    private void Countdown() {

        new Thread() {
            @Override
            public void run() {

                while (time > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (time == 0){
                                mPreview.setVisibility(View.GONE);
                            } else {
                                mTvTime.setText(time+"");
                                time--;
                            }
                        }
                    });
                }

            }
        }.start();
    }

    private void initData() {

        mCurrentIndex = Util.loadData(MainActivity.this)[Const.INDEX_LOAD_STAGE];

        mCurrentCoins = Util.loadData(MainActivity.this)[Const.INDEX_LOAD_COINS];

        delWordCoins = this.getResources().getInteger(R.integer.pay_delete_word);
        tipWordCoins = this.getResources().getInteger(R.integer.pay_tip_answer);
    }

    private void refreshData() {

        mCurrentSong = Const.getDatas(MainActivity.this).get(++mCurrentIndex);
        mViewCurrentCoins.setText(mCurrentCoins +"");

        mViewWordsContainer.removeAllViews();

        mSelectWords = initSelectWords();

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(100, 100);
        for (WordButton wordButton : mSelectWords) {

            mViewWordsContainer.addView(wordButton.mBtnWord, lp);
        }

        if (mCurrentStage != null) {
            mCurrentStage.setText((mCurrentIndex + 1) + "");
        }

        mAllWords = initAllWords();

        mAdapter = new MyGridAdapter();

        mGridView.setAdapter(mAdapter);

    }

    private List<WordButton> initAllWords() {

        List<WordButton> data = new ArrayList<WordButton>();

        List<String> wordList = getAllWords();
        for (int i = 0; i < WORD_COUNT; i++) {

            WordButton button = new WordButton();

            button.mStrWord = wordList.get(i);
            button.mIndex = i;

            data.add(button);
        }
        return data;
    }

    private List<WordButton> initSelectWords() {
        List<WordButton> data = new ArrayList<WordButton>();
        for (int i=0; i<mCurrentSong.getNameLength(); i++) {
            View view = Util.getView(MainActivity.this, R.layout.gridview_item);
            final WordButton wordButton = new WordButton();

            wordButton.mBtnWord = (Button) view.findViewById(R.id.id_btn_word);
            wordButton.mBtnWord.setTextColor(Color.WHITE);
            wordButton.mBtnWord.setText(wordButton.mStrWord);
            wordButton.mIsVisible = false;

            wordButton.mBtnWord.setBackgroundResource(R.mipmap.game_wordblank);
            wordButton.mBtnWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearSelectWord(wordButton);
                    mAllWords.get(wordButton.mIndex).mIsVisible = true;
                    mAllWords.get(wordButton.mIndex).mBtnWord.setVisibility(View.VISIBLE);
                }
            });

            data.add(wordButton);
        }
        return data;
    }

    private void initEvent() {

        mBtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handlePlayButton();
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showConfirmDialog(ID_DIALOG_BACK);

            }
        });

        mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog(ID_DIALOG_DEL);
            }
        });

        mBtnTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showConfirmDialog(ID_DIALOG_TIP);
            }
        });

        mDialogDelListener = new IAlertDialogButtonListener() {
            @Override
            public void onClick() {
                delOneWord();
            }
        };

        mDialogTipListener = new IAlertDialogButtonListener() {
            @Override
            public void onClick() {
                tipOneWord();
            }
        };

        mDialogLackListener = new IAlertDialogButtonListener() {
            @Override
            public void onClick() {

            }
        };

        mDialogBackListener = new IAlertDialogButtonListener() {
            @Override
            public void onClick() {
                if (mCurrentIndex == 0) {
                    finish();
                } else {
                    backStage();
                }

            }
        };

    }

    private void initView() {

        mGridView = (GridView) findViewById(R.id.id_gridview);

        mBtnStart = (ImageButton) findViewById(R.id.id_btn_start);
        mViewWordsContainer = (LinearLayout) findViewById(R.id.id_select_container);

        mViewCurrentCoins = (TextView) findViewById(R.id.id_tv_coins);

        mBtnDel = (ImageButton) findViewById(R.id.id_btn_delete);
        mBtnTip = (ImageButton) findViewById(R.id.id_btn_tip);

        mBtnBack = (ImageButton) findViewById(R.id.id_btn_back);

        mCurrentStage = (TextView) findViewById(R.id.id_tv_current_stage);

        mLayPass = findViewById(R.id.id_pass);
        mCurrentSongNamePass = (TextView) findViewById(R.id.id_tv_song_name);
        mCurrentStagePass = (TextView) findViewById(R.id.id_tv_pass);

        mPreview = findViewById(R.id.id_lay_preview);
        mTvTime = (TextView) findViewById(R.id.id_tv_time);

    }

    /**
     * 处理圆盘中间的播放按钮，就是开始播放音乐
     */
    private void handlePlayButton() {
        MyPlayer.playSong(MainActivity.this, mCurrentSong.getFileName());
    }

    private List<String> getAllWords() {
        List<String> wordList = new ArrayList<String>();
        for (int i=0; i<WORD_COUNT; i++) {
            String word = null;
            if (i < mCurrentSong.getNameLength()) {
                word = mCurrentSong.getSongName().charAt(i) + "";
            } else {
                word = getRandomWord();
            }
            wordList.add(word);
        }
        Collections.shuffle(wordList);
        return wordList;
    }

    private String getRandomWord() {
        String word = " ";
        int hightPos;
        int lowPos;
        Random random = new Random();
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            word = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return word;
    }

    class MyGridAdapter extends BaseAdapter {

        public MyGridAdapter() {

        }

        @Override
        public int getCount() {
            return mAllWords.size();
        }

        @Override
        public Object getItem(int position) {
            return mAllWords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            WordButton holder = null;
            Animation animation = null;

            if (view == null) {

                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.gridview_item, null);
                holder = mAllWords.get(position);
                holder.mBtnWord = (Button) view.findViewById(R.id.id_btn_word);

                view.setTag(holder);
            } else {
                holder = (WordButton) view.getTag();
            }
            animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale);
            animation.setStartOffset(position*100);
            view.startAnimation(animation);
            final WordButton finalHolder = holder;
            holder.mBtnWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickWords(finalHolder);
                }
            });

            holder.mBtnWord.setText(holder.mStrWord);
            Log.i("position", position+"");

            return view;

        }
    }

    private void clickWords(WordButton word) {
        setSelectWord(word);
        int checkResult = checkAnswer();
        if (checkResult == STATUS_RIGHT) {
            handlePassEvent();

        } else if (checkResult == STATUS_WRONG) {

            sparkWord();

        } else {

            for (WordButton wordButton:mSelectWords) {
                wordButton.mBtnWord.setTextColor(Color.WHITE);
            }

        }
    }

    private void setSelectWord(WordButton wordButton) {
        for (int i=0; i<mSelectWords.size(); i++) {
            WordButton selectWord = mSelectWords.get(i);
            if (selectWord.mStrWord.equals(" ")) {
                selectWord.mStrWord = wordButton.mStrWord;
                selectWord.mBtnWord.setText(selectWord.mStrWord);
                selectWord.mIsVisible = true;
                selectWord.mIndex = wordButton.mIndex;
                mSelectWords.set(i, selectWord);
                wordButton.mIsVisible = false;
                wordButton.mBtnWord.setVisibility(View.INVISIBLE);
                break;
            }
        }
    }

    private void clearSelectWord(WordButton wordButton) {
        wordButton.mStrWord = " ";
        wordButton.mBtnWord.setText(wordButton.mStrWord);
        wordButton.mIsVisible = false;

    }

    private int checkAnswer() {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<mSelectWords.size(); i++) {
            if (mSelectWords.get(i).mStrWord.equals(" ")) {
                return STATUS_LACK;
            }
            sb.append(mSelectWords.get(i).mStrWord);
        }
        return (sb.toString().equals(mCurrentSong.getSongName()))?STATUS_RIGHT:STATUS_WRONG;
    }

    private void sparkWord() {
        TimerTask task = new TimerTask() {

            boolean mChange = false;

            int spartTimes = 0;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (++spartTimes <= SPASH_TIMES) {
                            for (WordButton wordButton:mSelectWords) {
                                wordButton.mBtnWord.setTextColor(mChange?Color.RED:Color.WHITE);
                            }
                            mChange = !mChange;
                        }
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1, 200);
    }

    private void handlePassEvent() {

        mLayPass.setVisibility(View.VISIBLE);

        if (mCurrentStagePass != null) {
            mCurrentStagePass.setText((mCurrentIndex + 1) + "");
        }

        if (mCurrentSongNamePass != null) {
            mCurrentSongNamePass.setText(mCurrentSong.getSongName());
        }
        MyPlayer.stopSong(MainActivity.this);
        MyPlayer.playTone(MainActivity.this, Const.INDEX_TONE_COIN);
        ImageButton btnPass = (ImageButton) findViewById(R.id.id_btn_next);
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllPass()) {
                    //进入通关界面
                    mCurrentIndex = 0;
                    Util.showDialog(MainActivity.this, "恭喜您已全部通关，点击是退出游戏", mDialogBackListener);
                } else {
                    mLayPass.setVisibility(View.GONE);
                    mCurrentCoins+=30;
                    refreshData();
                }
            }
        });



//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setMessage("通过本关");
//        builder.setTitle("恭喜您");
//        builder.setPositiveButton("继续挑战", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//                //
//            }
//        });
//        builder.setNegativeButton("离开", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//                //
//            }
//        });
//        builder.create().show();
    }

    private void backStage() {
        mCurrentCoins-=30;
        mCurrentIndex-=2;
        refreshData();
    }

    private boolean handleCoins(int data) {
        if (mCurrentCoins + data >= 0) {
            return true;
        } else {
            return false;
        }

    }

    private void delOneWord() {
        if (!handleCoins(-delWordCoins)) {
            showConfirmDialog(ID_DIALOG_LACK);
            return;
        }
        WordButton wordButton = findNotAnswerWord();
        wordButton.mBtnWord.setVisibility(View.INVISIBLE);
        mCurrentCoins += -delWordCoins;
        mViewCurrentCoins.setText(mCurrentCoins +"");

    }

    private WordButton findNotAnswerWord() {
        Random random = new Random();
        WordButton buf = null;
        while(true) {
            int index = random.nextInt(WORD_COUNT);
            buf = mAllWords.get(index);
            if (buf.mIsVisible && !isAnswerWord(buf)) {
                return buf;
            }
        }
    }

    private boolean isAnswerWord(WordButton wordButton) {

        for (int i=0; i<mCurrentSong.getNameLength(); i++) {
            if (wordButton.mStrWord.equals(mCurrentSong.getSongName().charAt(i)+"")) {
                return true;
            }
        }
        return false;
    }

    int i = 0;
    private void tipOneWord() {
        if (!handleCoins(-tipWordCoins)) {
            showConfirmDialog(ID_DIALOG_LACK);
            return;
        }
        boolean tipWord = false;
        for (int i=0; i<mSelectWords.size(); i++) {
            if (mSelectWords.get(i).mStrWord.equals(" ")) {
                clickWords(findIsAnswerWord(i));
                tipWord = true;
                mCurrentCoins += -tipWordCoins;
                mViewCurrentCoins.setText(mCurrentCoins +"");
                break;
            }
        }
        if (!tipWord){
            return;
        }
    }

    private WordButton findIsAnswerWord(int index) {
        WordButton buf = null;
        for (int i=0; i<WORD_COUNT; i++) {
            buf = mAllWords.get(i);
            if (buf.mStrWord.equals(mCurrentSong.getNameCharacters()[index]+"")&&buf.mIsVisible) {
                return buf;
            }
        }
        return null;
    }

    /**
     * 判断是否通关
     * @return
     */
    private boolean isAllPass() {

        return (mCurrentIndex == Const.TOTAL_COUNTS - 1);
    }

    private void showConfirmDialog(int id) {
        switch (id) {
            case ID_DIALOG_DEL:
                Util.showDialog(MainActivity.this, "确认花掉" + delWordCoins + "个金币去掉一个错误答案", mDialogDelListener);
                break;
            case ID_DIALOG_TIP:
                Util.showDialog(MainActivity.this, "确认花掉" + tipWordCoins + "个金币获得一个文字提示", mDialogTipListener);
                break;
            case ID_DIALOG_LACK:
                Util.showDialog(MainActivity.this, "金币不足", mDialogLackListener);
            case ID_DIALOG_BACK:
                if (mCurrentIndex == 0) {
                    Util.showDialog(MainActivity.this, "确认退出游戏", mDialogBackListener);
                } else {
                    Util.showDialog(MainActivity.this, "确认返回上一关", mDialogBackListener);
                }

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.saveData(MainActivity.this, mCurrentIndex-1, mCurrentCoins);
        MyPlayer.stopSong(MainActivity.this);
    }
}
