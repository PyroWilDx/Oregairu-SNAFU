package aquaaxisorder.app.oregairusnafu.lightNovelPackage;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import aquaaxisorder.app.oregairusnafu.HomeActivity;
import aquaaxisorder.app.oregairusnafu.R;

import static android.content.Context.MODE_PRIVATE;

public class ReadNovelFragment extends Fragment {

    private SharedPreferences.Editor editor;
    private final int defaultMillis = 2500;
    private FloatingActionButton saveFloatingActionButton;
    private Toast savedPageToast;
    private HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.novel_read_fragment, container, false);

        PDFView volumeXPDF = view.findViewById(R.id.fragment_lightnovel_volumeX_pdf);
        savedPageToast = new Toast(getContext());

        if (getActivity() != null) {
            homeActivity = (HomeActivity) getActivity();
            homeActivity.closeKeyboard();
            homeActivity.toolbar.setVisibility(View.GONE);
            editor = this.getActivity().getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE).edit();
        }

        if (getArguments() != null) {
            Bundle args = getArguments();
            boolean landScape = args.getBoolean("LightNovelLandScape", true);
            boolean nightLightMode = args.getBoolean("LightNovelNightMode", false);
            String volumeNo = args.getString("LightNovelVolumeNO", "Oregairu1.pdf");
            int startPage = args.getInt("LightNovelStartPage", 1);
            if (landScape) {
                homeActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                homeActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }

            volumeXPDF.fromAsset(volumeNo)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(startPage - 1)
                    .enableAnnotationRendering(false)
                    .password(null)
                    .scrollHandle(new DefaultScrollHandle(getContext()))
                    .enableAntialiasing(true)
                    .spacing(0)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .pageSnap(false)
                    .pageFling(false)
                    .nightMode(nightLightMode)
                    .load();
        }

        saveFloatingActionButton = view.findViewById(R.id.fragment_lightnovel_save_button);
        saveFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                saveFloatingActionButton.show();
                if (getContext() != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    final EditText storePageEditText = new EditText(getContext());
                    storePageEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    InputFilter[] filterArray = new InputFilter[1];
                    filterArray[0] = new InputFilter.LengthFilter(3);
                    storePageEditText.setFilters(filterArray);
                    alert.setMessage("Page :");
                    alert.setTitle("Write your current page number to save it!");
                    alert.setView(storePageEditText);
                    alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                int storedPageNumber = Integer.parseInt(storePageEditText.getText().toString());
                                if (storedPageNumber != 0) {
                                    editor.putInt(getString(R.string.prefsStoredPage), storedPageNumber);
                                    editor.apply();
                                    savedPageToast.cancel();
                                    savedPageToast = Toast.makeText(getContext(), getString(R.string.toastSavedPage) + " " + storedPageNumber,
                                            Toast.LENGTH_SHORT);
                                    savedPageToast.show();
                                } else {
                                    savedPageToast.cancel();
                                    savedPageToast = Toast.makeText(getContext(), "0 is an invalid page number.",
                                            Toast.LENGTH_SHORT);
                                    savedPageToast.show();
                                }
                            } catch (NumberFormatException ignored) {
                                savedPageToast.cancel();
                                savedPageToast = Toast.makeText(getContext(), "No page were saved.",
                                        Toast.LENGTH_SHORT);
                                savedPageToast.show();
                            }
                            hideSaveButtonAfterXmilliSec(defaultMillis);
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            hideSaveButtonAfterXmilliSec(defaultMillis);
                        }
                    });
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            hideSaveButtonAfterXmilliSec(defaultMillis);
                        }
                    });
                    alert.show();
                }
            }
        });

        handler = new Handler();
        hideSaveButtonAfterXmilliSec(5000);

        volumeXPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFloatingActionButton.show();
                saveFloatingActionButton.setEnabled(true);
                hideSaveButtonAfterXmilliSec(defaultMillis);
            }
        });

        return view;
    }

    private Handler handler;

    private void hideSaveButtonAfterXmilliSec(int millis) {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                saveFloatingActionButton.hide();
            }
        }, millis);
    }

}