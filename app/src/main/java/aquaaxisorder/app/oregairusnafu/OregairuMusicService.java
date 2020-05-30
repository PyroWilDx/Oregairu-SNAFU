package aquaaxisorder.app.oregairusnafu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import aquaaxisorder.app.oregairusnafu.musicPackage.PlayingFragment;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.TinyDB;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.notificationReceiverPackage.MusicPlayerNotificationReceiverNext;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.notificationReceiverPackage.MusicPlayerNotificationReceiverPlayPause;
import aquaaxisorder.app.oregairusnafu.utilitiesPackage.notificationReceiverPackage.MusicPlayerNotificationReceiverPrevious;

import static aquaaxisorder.app.oregairusnafu.utilitiesPackage.App.CHANNEL_ID;

public class OregairuMusicService extends Service {

    private static OregairuMusicService instance;
    private final IBinder mBinder = new ServiceBinder();
    public MediaPlayer mPlayer;
    public int length = 0;
    private static final int NOTIFICATION_ID = 5_2_1_9_4_5; //Y_U_K_I_N_O  :)
    private final ArrayList<Integer> songsList = new ArrayList<>();
    public ArrayList<String> songsNameList = new ArrayList<>();
    public final ArrayList<Integer> songsImageList = new ArrayList<>();
    private final Random random = new Random();
    private SharedPreferences prefs;
    public final List<String> playlistNameList = Arrays.asList("All tracks", "Custom playlist", "Openings & Endings",
            "OST", "OST Zoku", "Drama CDs", "Character Songs", "Character Songs Zoku",
            "Mini soundtracks |Matsuri Out Tracks|", "Covers");
    public int chosenPlaylistNumber = -1;
    //-1 = all tracks; 0 = custom tracks; 1 = Op/Ed; 2 = OST; 3 = OST Zoku; 4 = Drama CD
    //5 = CharaSong; 6 = CharaSong Zoku; 7 = OutTracks; 8 = Covers;

    public OregairuMusicService() {
    }

    class ServiceBinder extends Binder {
        OregairuMusicService getService() {
            return OregairuMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        prefs = getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
        TinyDB tinyDB = new TinyDB(getBaseContext());
        if ((tinyDB.getListInt(getString(R.string.prefsCustomTracksList))).size() > 0) {
            chosenPlaylistNumber = 0;
            addCustomSongs();
        } else {
            chosenPlaylistNumber = -1;
            addAllMusics();
        }
        createShuffleList();
    }

    private NotificationCompat.Builder musicServiceNotification;
    private RemoteViews customNotifLayoutCollapsed;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int priority = NotificationCompat.PRIORITY_MAX;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            priority = NotificationManager.IMPORTANCE_MAX;
        }

        customNotifLayoutCollapsed = new RemoteViews(getPackageName(), R.layout.music_notif_collapsed);

        customNotifLayoutCollapsed.setTextViewText(R.id.music_notif_title_textview,
                songsNameList.get(atWhichMusicNumber));
        customNotifLayoutCollapsed.setTextViewText(R.id.music_notif_playlist_textview,
                playlistNameList.get(chosenPlaylistNumber + 1));
        customNotifLayoutCollapsed.setInt(R.id.music_notif_image,
                "setBackgroundResource", songsImageList.get(atWhichMusicNumber));

        Intent broadcastIntent1 = new Intent(this, MusicPlayerNotificationReceiverPrevious.class);
        PendingIntent actionIntent1 = PendingIntent.getBroadcast(this, 0, broadcastIntent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        customNotifLayoutCollapsed.setOnClickPendingIntent(R.id.music_notif_previous_button, actionIntent1);
        Intent broadcastIntent2 = new Intent(this, MusicPlayerNotificationReceiverPlayPause.class);
        PendingIntent actionIntent2 = PendingIntent.getBroadcast(this, 0, broadcastIntent2,
                PendingIntent.FLAG_UPDATE_CURRENT);
        customNotifLayoutCollapsed.setOnClickPendingIntent(R.id.music_notif_playpause_button, actionIntent2);
        Intent broadcastIntent3 = new Intent(this, MusicPlayerNotificationReceiverNext.class);
        PendingIntent actionIntent3 = PendingIntent.getBroadcast(this, 0, broadcastIntent3,
                PendingIntent.FLAG_UPDATE_CURRENT);
        customNotifLayoutCollapsed.setOnClickPendingIntent(R.id.music_notif_next_button, actionIntent3);

        musicServiceNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_oregairu_svg_logo)
                .setPriority(priority)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0))
                .setCustomContentView(customNotifLayoutCollapsed)
                .setCustomBigContentView(customNotifLayoutCollapsed)
                .setOnlyAlertOnce(true)
                .setVibrate(new long[]{0})
                .setSound(null);

        startForeground(NOTIFICATION_ID, musicServiceNotification.build());

        return START_NOT_STICKY;
    }

    private void updateAndSendMusicPlayerNotification() {
        try {
            customNotifLayoutCollapsed.setTextViewText(R.id.music_notif_title_textview,
                    songsNameList.get(atWhichMusicNumber));
            customNotifLayoutCollapsed.setTextViewText(R.id.music_notif_playlist_textview,
                    playlistNameList.get(chosenPlaylistNumber + 1));
            customNotifLayoutCollapsed.setInt(R.id.music_notif_image,
                    "setBackgroundResource", songsImageList.get(atWhichMusicNumber));
            startForeground(NOTIFICATION_ID, musicServiceNotification.build());
        } catch (NullPointerException ignored) {
        }
    }

    public void updateNotifPlayPauseButtons() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                customNotifLayoutCollapsed.setInt(R.id.music_notif_playpause_button,
                        "setBackgroundResource", R.drawable.icon_pause_notif);
            } else {
                customNotifLayoutCollapsed.setInt(R.id.music_notif_playpause_button,
                        "setBackgroundResource", R.drawable.icon_play_notif);
            }
            startForeground(NOTIFICATION_ID, musicServiceNotification.build());
        }
    }

    public static OregairuMusicService getInstance() {
        return instance;
    }

    public int atWhichMusicNumber = 0;

    public void getWantedMusicToPlay(int musicNumber) {
        try {
            stopMusic();
        } catch (NullPointerException ignored) {
        }
        atWhichMusicNumber = musicNumber;
        playMusic();
    }

    private void playMusic() {
        mPlayer = MediaPlayer.create(this, songsList.get(atWhichMusicNumber));
        mPlayer.setVolume(prefs.getInt(getString(R.string.prefsVolumeLevel), 100) * 0.01f,
                prefs.getInt(getString(R.string.prefsVolumeLevel), 100) * 0.01f);
        mPlayer.start();
        updateAndSendMusicPlayerNotification();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextTrack();
            }
        });
        if (prefs.getBoolean(getString(R.string.prefsMusicPaused), false)) {
            pauseMusic();
        }
    }

    public void nextTrack() {
        if (atWhichMusicNumber < songsList.size() - 1) {
            getWantedMusicToPlay(atWhichMusicNumber + 1);
        } else {
            getWantedMusicToPlay(0);
        }
    }

    public void previousTrack() {
        if (atWhichMusicNumber > 0) {
            getWantedMusicToPlay(atWhichMusicNumber - 1);
        } else {
            getWantedMusicToPlay(songsList.size() - 1);
        }

    }

    public void createShuffleList() {
        int listSize = songsList.size();
        int randomNumber;
        ArrayList<Integer> randomSongsList = new ArrayList<>();
        ArrayList<String> randomSongsNameList = new ArrayList<>();
        ArrayList<Integer> randomSongsImageList = new ArrayList<>();
        for (int number = 0; number < listSize; number++) {
            randomNumber = random.nextInt(songsList.size());
            randomSongsList.add(songsList.get(randomNumber));
            randomSongsNameList.add(songsNameList.get(randomNumber));
            randomSongsImageList.add(songsImageList.get(randomNumber));
            songsList.remove(randomNumber);
            songsNameList.remove(randomNumber);
            songsImageList.remove(randomNumber);
        }
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();
        songsList.addAll(randomSongsList);
        songsNameList.addAll(randomSongsNameList);
        songsImageList.addAll(randomSongsImageList);

        getWantedMusicToPlay(0);
    }

    public void pauseMusic() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                length = mPlayer.getCurrentPosition();
                if (PlayingFragment.getInstance() != null) {
                    PlayingFragment.getInstance().updateViewForPlayingMusic();
                }
            }
        }
    }

    public void resumeMusic() {
        if (mPlayer != null) {
            if (!mPlayer.isPlaying()) {
                mPlayer.seekTo(length);
                mPlayer.start();
                if (PlayingFragment.getInstance() != null) {
                    PlayingFragment.getInstance().updateViewForPlayingMusic();
                }
            }
        }
    }

    public void stopMusic() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
    }

    public void addCustomSongs() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addAllMusics();
        songsNameList.clear();
        ArrayList<Integer> allTracksList = new ArrayList<>(songsList);
        songsList.clear();

        ArrayList<Integer> allTracksImageList = new ArrayList<>(songsImageList);
        songsImageList.clear();

        TinyDB tinyDB = new TinyDB(getBaseContext());
        ArrayList<Integer> customList;
        customList = tinyDB.getListInt(getString(R.string.prefsCustomTracksList));
        for (int number = 0; number < customList.size(); number++) {
            songsList.add(allTracksList.get(customList.get(number)));
        }

        songsNameList = addTracksTitlesToList(0);

        for (int number = 0; number < customList.size(); number++) {
            songsImageList.add(allTracksImageList.get(customList.get(number)));
        }
    }

    public void addOpeningsEndings() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(1);

        songsNameList = addTracksTitlesToList(1);

        addTracksPicturesToList(R.drawable.music_harumodoki_cover, 1);
    }

    public void addOsts() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(2);

        songsNameList = addTracksTitlesToList(2);

        addTracksPicturesToList(R.drawable.music_ost_cover, 2);
    }

    public void addOstsZoku() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(3);

        songsNameList = addTracksTitlesToList(3);

        addTracksPicturesToList(R.drawable.music_ostzoku_cover, 3);
    }

    public void addDramaCD() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(4);

        songsNameList = addTracksTitlesToList(4);

        addTracksPicturesToList(R.drawable.music_cd_3_cover, 4);
    }

    public void addCharaSongs() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(5);

        songsNameList = addTracksTitlesToList(5);

        addTracksPicturesToList(R.drawable.music_charasong_cover, 5);
    }

    public void addCharaSongsZoku() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(6);

        songsNameList = addTracksTitlesToList(6);

        addTracksPicturesToList(R.drawable.music_charasongzoku_cover, 6);
    }

    public void addOutTracks() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(7);

        songsNameList = addTracksTitlesToList(7);

        addTracksPicturesToList(R.drawable.music_outtrack_cover, 7);
    }

    public void addCovers() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(8);

        songsNameList = addTracksTitlesToList(8);

        addTracksPicturesToList(R.drawable.music_festival_music, 8);
    }

    public void addAllMusics() {
        songsList.clear();
        songsNameList.clear();
        songsImageList.clear();

        addTracksToList(-1);

        songsNameList = addTracksTitlesToList(-1);

        addTracksPicturesToList(R.drawable.music_harumodoki_cover, 1);
        addTracksPicturesToList(R.drawable.music_ost_cover, 2);
        addTracksPicturesToList(R.drawable.music_ostzoku_cover, 3);
        addTracksPicturesToList(R.drawable.music_cd_3_cover, 4);
        addTracksPicturesToList(R.drawable.music_charasong_cover, 5);
        addTracksPicturesToList(R.drawable.music_charasongzoku_cover, 6);
        addTracksPicturesToList(R.drawable.music_outtrack_cover, 7);
        addTracksPicturesToList(R.drawable.music_festival_music, 8);
    }

    private void addTracksToList(int playlistNo) {
        switch (playlistNo) {
            case -1:
                songsList.add(R.raw.harumodoki_op);
                songsList.add(R.raw.yukitoki_op);
                songsList.add(R.raw.everydayworld_ed);
                songsList.add(R.raw.everydayworldyuiver_ed);
                songsList.add(R.raw.everydayworldyukinover_ed);
                songsList.add(R.raw.helloalone_ed);
                songsList.add(R.raw.helloalonebandearrange_ed_charasong);
                songsList.add(R.raw.helloaloneyuiballade_ed);

                songsList.add(R.raw.anatatachiwa_ost);
                songsList.add(R.raw.bocchinoryuugi_ost);
                songsList.add(R.raw.chainmail_ost);
                songsList.add(R.raw.damesugiruhitotachi_ost);
                songsList.add(R.raw.fightreadygo_ost);
                songsList.add(R.raw.fukamarutairitsu_ost);
                songsList.add(R.raw.fuonnakuuki_ost);
                songsList.add(R.raw.hangeki_ost);
                songsList.add(R.raw.honneienakute_ost);
                songsList.add(R.raw.honoonojoou_ost);
                songsList.add(R.raw.houshibukatsudounisshi01_ost);
                songsList.add(R.raw.houshibukatsudounisshi02_ost);
                songsList.add(R.raw.itsunomohoukago_ost);
                songsList.add(R.raw.jikaiyokoku_ost);
                songsList.add(R.raw.kekkaallright_ost);
                songsList.add(R.raw.kittokanojowa_ost);
                songsList.add(R.raw.kokorosurechigau_ost);
                songsList.add(R.raw.koorinojoou_ost);
                songsList.add(R.raw.makotoniikannagarashiriai_ost);
                songsList.add(R.raw.monologue_ost);
                songsList.add(R.raw.moumodoritakunaianonatsunohi_ost);
                songsList.add(R.raw.nankalya_ost);
                songsList.add(R.raw.ninwadareshimokanpekidewanaikara_ost);
                songsList.add(R.raw.orenoturn_ost);
                songsList.add(R.raw.resetbutton_ost);
                songsList.add(R.raw.riajubakuhatsushiro_ost);
                songsList.add(R.raw.saidainopinch_ost);
                songsList.add(R.raw.schoolcastle_ost);
                songsList.add(R.raw.seishunlovecomenokamisama_ost);
                songsList.add(R.raw.sophisticatedgirl_ost);
                songsList.add(R.raw.totsukawaii_ost);
                songsList.add(R.raw.toufumentalkikiippatsu_ost);
                songsList.add(R.raw.waretowoomode_ost);
                songsList.add(R.raw.yahallo_ost);
                songsList.add(R.raw.yasashiionnanoko_ost);
                songsList.add(R.raw.youkosohoushibuhe_ost);
                songsList.add(R.raw.zaimokuzaoverdrive_ost);

                songsList.add(R.raw.chikakarazutookarazu_ostzoku);
                songsList.add(R.raw.fugourinakanjou_ostzoku);
                songsList.add(R.raw.hitonokimochimottokangaeteyo_ostzoku);
                songsList.add(R.raw.iroha_ostzoku);
                songsList.add(R.raw.jibunnohontounokimochiwa_ostzoku);
                songsList.add(R.raw.ketsuretsu_ostzoku);
                songsList.add(R.raw.mimamotteitekureruhito_ostzoku);
                songsList.add(R.raw.nakanaori_ostzoku);
                songsList.add(R.raw.nisemononichijou_ostzoku);
                songsList.add(R.raw.repurika_ostzoku);
                songsList.add(R.raw.sannindeirujikan_ostzoku);
                songsList.add(R.raw.tairitsu_ostzoku);
                songsList.add(R.raw.tokubetsunahint_ostzoku);
                songsList.add(R.raw.tsunagitometasekai_ostzoku);
                songsList.add(R.raw.winwin_ostzoku);
                songsList.add(R.raw.yuinoketsui_ostzoku);
                songsList.add(R.raw.yukidoke_ostzoku);

                songsList.add(R.raw.brightgeneration_cddrama);
                songsList.add(R.raw.merrychristmaswithyou_cddrama);
                songsList.add(R.raw.rockyou_cddrama);

                songsList.add(R.raw.bitterbittersweet_charasong);
                songsList.add(R.raw.bokutachisengen_charasong);
                songsList.add(R.raw.goingoingaloneway_charasong);
                songsList.add(R.raw.himawarigooddays_charasong);
                songsList.add(R.raw.naitenankanaitobaccogameni_charasong);
                songsList.add(R.raw.smilegoround_charasong);
                songsList.add(R.raw.yukidokenisaitahana_charasong);

                songsList.add(R.raw.bokutachidiary_charasongzoku);
                songsList.add(R.raw.enjoygomyway_charasongzoku);
                songsList.add(R.raw.happyendnosobade_charasongzoku);
                songsList.add(R.raw.honmonogahoshikereba_charasongzoku);
                songsList.add(R.raw.itsukakimigaotonaninarumade_charasongzoku);
                songsList.add(R.raw.kashikogirl_charasongzoku);
                songsList.add(R.raw.kawarusoranoshita_charasongzoku);
                songsList.add(R.raw.kiminicrescendo_charasongzoku);
                songsList.add(R.raw.letsgoalldaylong_charasongzoku);

                songsList.add(R.raw.chibasen_outtracks);
                songsList.add(R.raw.hoshinouujisama_outtracks);
                songsList.add(R.raw.maboroshinogoalin_outtracks);
                songsList.add(R.raw.teamworkbacchiridane_outtracks);
                songsList.add(R.raw.zaimokuzacrusher_outtracks);

                songsList.add(R.raw.everydayworld_pianocover);
                songsList.add(R.raw.harumodoki_pianocover);
                songsList.add(R.raw.helloalone_pianocover);
                songsList.add(R.raw.yukitoki_pianocover);
                break;
            case 0:
                break;
            case 1:
                songsList.add(R.raw.harumodoki_op);
                songsList.add(R.raw.yukitoki_op);
                songsList.add(R.raw.everydayworld_ed);
                songsList.add(R.raw.everydayworldyuiver_ed);
                songsList.add(R.raw.everydayworldyukinover_ed);
                songsList.add(R.raw.helloalone_ed);
                songsList.add(R.raw.helloalonebandearrange_ed_charasong);
                songsList.add(R.raw.helloaloneyuiballade_ed);
                break;
            case 2:
                songsList.add(R.raw.anatatachiwa_ost);
                songsList.add(R.raw.bocchinoryuugi_ost);
                songsList.add(R.raw.chainmail_ost);
                songsList.add(R.raw.damesugiruhitotachi_ost);
                songsList.add(R.raw.fightreadygo_ost);
                songsList.add(R.raw.fukamarutairitsu_ost);
                songsList.add(R.raw.fuonnakuuki_ost);
                songsList.add(R.raw.hangeki_ost);
                songsList.add(R.raw.honneienakute_ost);
                songsList.add(R.raw.honoonojoou_ost);
                songsList.add(R.raw.houshibukatsudounisshi01_ost);
                songsList.add(R.raw.houshibukatsudounisshi02_ost);
                songsList.add(R.raw.itsunomohoukago_ost);
                songsList.add(R.raw.jikaiyokoku_ost);
                songsList.add(R.raw.kekkaallright_ost);
                songsList.add(R.raw.kittokanojowa_ost);
                songsList.add(R.raw.kokorosurechigau_ost);
                songsList.add(R.raw.koorinojoou_ost);
                songsList.add(R.raw.makotoniikannagarashiriai_ost);
                songsList.add(R.raw.monologue_ost);
                songsList.add(R.raw.moumodoritakunaianonatsunohi_ost);
                songsList.add(R.raw.nankalya_ost);
                songsList.add(R.raw.ninwadareshimokanpekidewanaikara_ost);
                songsList.add(R.raw.orenoturn_ost);
                songsList.add(R.raw.resetbutton_ost);
                songsList.add(R.raw.riajubakuhatsushiro_ost);
                songsList.add(R.raw.saidainopinch_ost);
                songsList.add(R.raw.schoolcastle_ost);
                songsList.add(R.raw.seishunlovecomenokamisama_ost);
                songsList.add(R.raw.sophisticatedgirl_ost);
                songsList.add(R.raw.totsukawaii_ost);
                songsList.add(R.raw.toufumentalkikiippatsu_ost);
                songsList.add(R.raw.waretowoomode_ost);
                songsList.add(R.raw.yahallo_ost);
                songsList.add(R.raw.yasashiionnanoko_ost);
                songsList.add(R.raw.youkosohoushibuhe_ost);
                songsList.add(R.raw.zaimokuzaoverdrive_ost);
                break;
            case 3:
                songsList.add(R.raw.chikakarazutookarazu_ostzoku);
                songsList.add(R.raw.fugourinakanjou_ostzoku);
                songsList.add(R.raw.hitonokimochimottokangaeteyo_ostzoku);
                songsList.add(R.raw.iroha_ostzoku);
                songsList.add(R.raw.jibunnohontounokimochiwa_ostzoku);
                songsList.add(R.raw.ketsuretsu_ostzoku);
                songsList.add(R.raw.mimamotteitekureruhito_ostzoku);
                songsList.add(R.raw.nakanaori_ostzoku);
                songsList.add(R.raw.nisemononichijou_ostzoku);
                songsList.add(R.raw.repurika_ostzoku);
                songsList.add(R.raw.sannindeirujikan_ostzoku);
                songsList.add(R.raw.tairitsu_ostzoku);
                songsList.add(R.raw.tokubetsunahint_ostzoku);
                songsList.add(R.raw.tsunagitometasekai_ostzoku);
                songsList.add(R.raw.winwin_ostzoku);
                songsList.add(R.raw.yuinoketsui_ostzoku);
                songsList.add(R.raw.yukidoke_ostzoku);
                break;
            case 4:
                songsList.add(R.raw.brightgeneration_cddrama);
                songsList.add(R.raw.merrychristmaswithyou_cddrama);
                songsList.add(R.raw.rockyou_cddrama);
                break;
            case 5:
                songsList.add(R.raw.bitterbittersweet_charasong);
                songsList.add(R.raw.bokutachisengen_charasong);
                songsList.add(R.raw.goingoingaloneway_charasong);
                songsList.add(R.raw.himawarigooddays_charasong);
                songsList.add(R.raw.naitenankanaitobaccogameni_charasong);
                songsList.add(R.raw.smilegoround_charasong);
                songsList.add(R.raw.yukidokenisaitahana_charasong);
                break;
            case 6:
                songsList.add(R.raw.bokutachidiary_charasongzoku);
                songsList.add(R.raw.enjoygomyway_charasongzoku);
                songsList.add(R.raw.happyendnosobade_charasongzoku);
                songsList.add(R.raw.honmonogahoshikereba_charasongzoku);
                songsList.add(R.raw.itsukakimigaotonaninarumade_charasongzoku);
                songsList.add(R.raw.kashikogirl_charasongzoku);
                songsList.add(R.raw.kawarusoranoshita_charasongzoku);
                songsList.add(R.raw.kiminicrescendo_charasongzoku);
                songsList.add(R.raw.letsgoalldaylong_charasongzoku);
                break;
            case 7:
                songsList.add(R.raw.chibasen_outtracks);
                songsList.add(R.raw.hoshinouujisama_outtracks);
                songsList.add(R.raw.maboroshinogoalin_outtracks);
                songsList.add(R.raw.teamworkbacchiridane_outtracks);
                songsList.add(R.raw.zaimokuzacrusher_outtracks);
                break;
            case 8:
                songsList.add(R.raw.everydayworld_pianocover);
                songsList.add(R.raw.harumodoki_pianocover);
                songsList.add(R.raw.helloalone_pianocover);
                songsList.add(R.raw.yukitoki_pianocover);
                break;

        }
    }

    public ArrayList<String> addTracksTitlesToList(int whichPlaylist) {
        ArrayList<String> list = new ArrayList<>();
        switch (whichPlaylist) {
            case -1:
                list.add("Harumodoki - Opening");
                list.add("Yukitoki - Opening");
                list.add("Everyday World - Ending");
                list.add("Everyday World Yui solo ver. - Ending");
                list.add("Everyday World Yukino solo ver. - Ending");
                list.add("Hello Alone - Ending");
                list.add("Hello Alone Bande Arrange - Ending");
                list.add("Hello Alone Yui Ballade - Ending");

                list.add("Anata Tachi wa - OST");
                list.add("Bocchi no Ryuugi - OST");
                list.add("Chain Mail - OST");
                list.add("Dame Sugiru Hitotachi - OST");
                list.add("Fight, Ready Go! - OST");
                list.add("Fukamaru Tairitsu - OST");
                list.add("Fuon na Kuuki - OST");
                list.add("Hangeki - OST");
                list.add("Honne Ienakute - OST");
                list.add("Honoo no Joou - OST");
                list.add("Houshibu Katsudou Nisshi 01 - OST");
                list.add("Houshibu Katsudou Nisshi 02 - OST");
                list.add("Itsunomo Houkago - OST");
                list.add("Jikai Yokoku - OST");
                list.add("Kekka Orai? - OST");
                list.add("Kitto Kanojo wa - OST");
                list.add("Kokoro Surechigau - OST");
                list.add("Koori no Joou - OST");
                list.add("Makotoni Ikan Nagara, Shiriai - OST");
                list.add("Monologue - OST");
                list.add("Mou Modoritakunai, Ano Natsu no Hi - OST");
                list.add("Nanka, Iya - OST");
                list.add("Nin wa Dareshimo, Kanpeki de wa nai Kara - OST");
                list.add("Ore no Turn - OST");
                list.add("Reset Button - OST");
                list.add("Riajuu Bakuhatsushiro. - OST");
                list.add("Saidai no Pinch - OST");
                list.add("School Caste - OST");
                list.add("Seishun Love Comedy no Kamisama - OST");
                list.add("sophisticated girl - OST");
                list.add("Totsu Kawaii - OST");
                list.add("Toufu Mental Kiki Ippatsu - OST");
                list.add("Ware to Onushi de - OST");
                list.add("Yahallo! - OST");
                list.add("Yasashii Onnanoko - OST");
                list.add("Youkoso Houshibu e - OST");
                list.add("Zaimokuza Overdrive - OST");

                list.add("Chikakarazu Tookarazu - OST Zoku");
                list.add("Fugouri na Kanjou - OST Zoku");
                list.add("Hito no Kimochi, Motto Kangaete yo - OST Zoku");
                list.add("Iroha - OST Zoku");
                list.add("Jibun no Hontou no Kimochi wa - OST Zoku");
                list.add("Ketsuretsu - OST Zoku");
                list.add("Mimamotteite Kureru Hito - OST Zoku");
                list.add("Nakanaori - OST Zoku");
                list.add("Nisemono Nichijou - OST Zoku");
                list.add("Repurika - OST Zoku");
                list.add("Sannin de Iru Jikan - OST Zoku");
                list.add("Tairitsu - OST Zoku");
                list.add("Tokubetsu na Hint - OST Zoku");
                list.add("Tsunagitometa Sekai - OST Zoku");
                list.add("WIN-WIN - OST Zoku");
                list.add("Yui no Ketsui - OST Zoku");
                list.add("Yukidoke - OST Zoku");

                list.add("Bright Generation - Drama CD");
                list.add("Merry Christmas with you - Drama CD");
                list.add("Rock You ! - Drama CD");

                list.add("Bitter Bitter Sweet - Character Song");
                list.add("Bokutachi Sengen  - Character Song");
                list.add("Going Going Alone way ! - Character Song");
                list.add("Himawari Good Days - Character Song");
                list.add("Naite Nanka nai. ~Tabako ga Me ni,,,~ - Character Song");
                list.add("Smile Go Round - Character Song");
                list.add("Yukidoke ni Saita Hana - Character Song");

                list.add("Bokutachi Diary - Character Song Zoku");
                list.add("Enjoy!! go my way - Character Song Zoku");
                list.add("Happy End no Soba de - Character Song Zoku");
                list.add("Honmono ga Hoshikereba - Character Song Zoku");
                list.add("Itsuka Kimi ga Otona ni Naru made - Character Song Zoku");
                list.add("Kashiko Girl - Character Song Zoku");
                list.add("Kawaru Sora no Shita - Character Song Zoku");
                list.add("Kimi ni Crescendo - Character Song Zoku");
                list.add("Let\'s go! All day long! - Character Song Zoku");

                list.add("Hoshi no Oujisama - Mini Soundtrack |Matsuri Out Tracks|");
                list.add("Chibasen! - Mini Soundtrack |Matsuri Out Tracks|");
                list.add("Zaimokuza Crusher - Mini Soundtrack |Matsuri Out Tracks|");
                list.add("Teamwork wa Bacchiri da ne♪ - Mini Soundtrack |Matsuri Out Tracks|");
                list.add("Maboroshi no Goal in!? - Mini Soundtrack |Matsuri Out Tracks|");

                list.add("Everyday World - Piano cover by Animenz Piano Sheets (YT)");
                list.add("Harumodoki - Piano cover by Fonzi M, Melodies on Piano (YT)");
                list.add("Hello Alone - Piano cover by Animenz Piano Sheets (YT)");
                list.add("Yukitoki - Piano cover by Zacky The Pianist (YT)");
                return list;
            case 0:
                TinyDB tinyDB = new TinyDB(getBaseContext());
                ArrayList<Integer> customList;
                customList = tinyDB.getListInt(getString(R.string.prefsCustomTracksList));
                ArrayList<String> allTrackTitleList = new ArrayList<>(addTracksTitlesToList(-1));
                for (int number = 0; number < customList.size(); number++) {
                    list.add(allTrackTitleList.get(customList.get(number)));
                }
                return list;
            case 1:
                list.add("Harumodoki - Opening");
                list.add("Yukitoki - Opening");
                list.add("Everyday World - Ending");
                list.add("Everyday World Yui solo ver. - Ending");
                list.add("Everyday World Yukino solo ver. - Ending");
                list.add("Hello Alone - Ending");
                list.add("Hello Alone Bande Arrange - Ending");
                list.add("Hello Alone Yui Ballade - Ending");
                return list;
            case 2:
                list.add("Anata Tachi wa - OST");
                list.add("Bocchi no Ryuugi - OST");
                list.add("Chain Mail - OST");
                list.add("Dame Sugiru Hitotachi - OST");
                list.add("Fight, Ready Go! - OST");
                list.add("Fukamaru Tairitsu - OST");
                list.add("Fuon na Kuuki - OST");
                list.add("Hangeki - OST");
                list.add("Honne Ienakute - OST");
                list.add("Honoo no Joou - OST");
                list.add("Houshibu Katsudou Nisshi 01 - OST");
                list.add("Houshibu Katsudou Nisshi 02 - OST");
                list.add("Itsunomo Houkago - OST");
                list.add("Jikai Yokoku - OST");
                list.add("Kekka Orai? - OST");
                list.add("Kitto Kanojo wa - OST");
                list.add("Kokoro Surechigau - OST");
                list.add("Koori no Joou - OST");
                list.add("Makotoni Ikan Nagara, Shiriai - OST");
                list.add("Monologue - OST");
                list.add("Mou Modoritakunai, Ano Natsu no Hi - OST");
                list.add("Nanka, Iya - OST");
                list.add("Nin wa Dareshimo, Kanpeki de wa nai Kara - OST");
                list.add("Ore no Turn - OST");
                list.add("Reset Button - OST");
                list.add("Riajuu Bakuhatsushiro. - OST");
                list.add("Saidai no Pinch - OST");
                list.add("School Caste - OST");
                list.add("Seishun Love Comedy no Kamisama - OST");
                list.add("sophisticated girl - OST");
                list.add("Totsu Kawaii - OST");
                list.add("Toufu Mental Kiki Ippatsu - OST");
                list.add("Ware to Onushi de - OST");
                list.add("Yahallo! - OST");
                list.add("Yasashii Onnanoko - OST");
                list.add("Youkoso Houshibu e - OST");
                list.add("Zaimokuza Overdrive - OST");
                return list;
            case 3:
                list.add("Chikakarazu Tookarazu - OST Zoku");
                list.add("Fugouri na Kanjou - OST Zoku");
                list.add("Hito no Kimochi, Motto Kangaete yo - OST Zoku");
                list.add("Iroha - OST Zoku");
                list.add("Jibun no Hontou no Kimochi wa - OST Zoku");
                list.add("Ketsuretsu - OST Zoku");
                list.add("Mimamotteite Kureru Hito - OST Zoku");
                list.add("Nakanaori - OST Zoku");
                list.add("Nisemono Nichijou - OST Zoku");
                list.add("Repurika - OST Zoku");
                list.add("Sannin de Iru Jikan - OST Zoku");
                list.add("Tairitsu - OST Zoku");
                list.add("Tokubetsu na Hint - OST Zoku");
                list.add("Tsunagitometa Sekai - OST Zoku");
                list.add("WIN-WIN - OST Zoku");
                list.add("Yui no Ketsui - OST Zoku");
                list.add("Yukidoke - OST Zoku");
                return list;
            case 4:
                list.add("Bright Generation - Drama CD");
                list.add("Merry Christmas with you - Drama CD");
                list.add("Rock You ! - Drama CD");
                return list;
            case 5:
                list.add("Bitter Bitter Sweet - Character Song");
                list.add("Bokutachi Sengen  - Character Song");
                list.add("Going Going Alone way ! - Character Song");
                list.add("Himawari Good Days - Character Song");
                list.add("Naite Nanka nai. ~Tabako ga Me ni,,,~ - Character Song");
                list.add("Smile Go Round - Character Song");
                list.add("Yukidoke ni Saita Hana - Character Song");
                return list;
            case 6:
                list.add("Bokutachi Diary - Character Song Zoku");
                list.add("Enjoy!! go my way - Character Song Zoku");
                list.add("Happy End no Soba de - Character Song Zoku");
                list.add("Honmono ga Hoshikereba - Character Song Zoku");
                list.add("Itsuka Kimi ga Otona ni Naru made - Character Song Zoku");
                list.add("Kashiko Girl - Character Song Zoku");
                list.add("Kawaru Sora no Shita - Character Song Zoku");
                list.add("Kimi ni Crescendo - Character Song Zoku");
                list.add("Let\'s go! All day long! - Character Song Zoku");
                return list;
            case 7:
                list.add("Hoshi no Oujisama - Mini Soundtrack |Matsuri Out Tracks|");
                list.add("Chibasen! - Mini Soundtrack |Matsuri Out Tracks|");
                list.add("Zaimokuza Crusher - Mini Soundtrack |Matsuri Out Tracks|");
                list.add("Teamwork wa Bacchiri da ne♪ - Mini Soundtrack |Matsuri Out Tracks|");
                list.add("Maboroshi no Goal in!? - Mini Soundtrack |Matsuri Out Tracks|");
                return list;
            case 8:
                list.add("Everyday World - Piano cover by Animenz Piano Sheets (YT)");
                list.add("Harumodoki - Piano cover by Fonzi M, Melodies on Piano (YT)");
                list.add("Hello Alone - Piano cover by Animenz Piano Sheets (YT)");
                list.add("Yukitoki - Piano cover by Zacky The Pianist (YT)");
                return list;
        }
        return list;
    }

    private void addTracksPicturesToList(int picture, int playlistNumber) {
        if (playlistNumber == 1) {
            songsImageList.add(R.drawable.music_harumodoki_cover);
            songsImageList.add(R.drawable.music_yukitoki_cover);
            songsImageList.add(R.drawable.music_everydayworld_cover);
            songsImageList.add(R.drawable.music_yui_everydayworld);
            songsImageList.add(R.drawable.music_yukino_everydayworld);
            songsImageList.add(R.drawable.music_helloalone_cover);
            songsImageList.add(R.drawable.music_helloalone_cover);
            songsImageList.add(R.drawable.music_helloalone_cover);
        } else {
            for (int number = 0; number < addTracksTitlesToList(playlistNumber).size(); number++) {
                songsImageList.add(picture);
            }
        }
    }

}
