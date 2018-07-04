package de.brick6.nxtremote.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import de.brick6.nxtremote.R;
import de.brick6.nxtremote.logging.LogObserver;
import de.brick6.nxtremote.logging.Logger;

public class LogFragment extends Fragment implements LogObserver {

  Logger logger;
  EditText androidLog;
  EditText robotLog;
  /**
   * The fragment argument representing the section number for this fragment.
   */
  private static final String ARG_SECTION_NUMBER = "0";

  public LogFragment() {

  }

  /**
   * Returns a new instance of this fragment for the given section number.
   */
  public static LogFragment newInstance(int sectionNumber) {
    LogFragment fragment = new LogFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_SECTION_NUMBER, sectionNumber);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    logger = Logger.getInstance();
    View rootView = inflater.inflate(R.layout.log_fragment, container, false);
    return rootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    androidLog = getView().findViewById(R.id.androidlog);

    robotLog = getView().findViewById(R.id.robotlog);

    androidLog.setKeyListener(null);
    robotLog.setKeyListener(null);

    logger.addObserver(this);
  }


  @Override
  public void logForAndroid(String message) {
    getActivity().runOnUiThread(() -> androidLog.append(message + "\n"));
  }


  @Override
  public void logForRobot(String message) {
    getActivity().runOnUiThread(() -> robotLog.append(message + "\n"));
  }
}


