package de.brick6.nxtremote.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import de.brick6.nxtremote.R;
import de.brick6.nxtremote.communication.AndroidObserver;
import de.brick6.nxtremote.communication.BluetoothHandler;
import de.brick6.nxtremote.communication.BluetoothObservable;
import de.brick6.nxtremote.communication.BluetoothState;
import de.brick6.nxtremote.logging.Logger;
import java.util.ArrayList;
import messaging.Command;
import messaging.CommandTypes;
import messaging.StateData;
import messaging.Strategies;
import messaging.StrategyHelper;

public class ControlFragment extends Fragment implements AndroidObserver, RobotStateChangeObserver {


  /**
   * The fragment argument representing the section number for this fragment.
   */
  private static final String ARG_SECTION_NUMBER = "1";

  private Logger logger;
  private Button connect;
  private Spinner strategyChooser;
  private TextView bluetoothStateView;
  private SeekBar speedChooser;
  private Switch autoSwitchToPid;
  private CommandHandler handler;

  public ControlFragment() {
    handler = new CommandHandler();
  }


  /**
   * Returns a new instance of this fragment for the given section number.
   */
  public static ControlFragment newInstance(int sectionNumber) {
    ControlFragment fragment = new ControlFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_SECTION_NUMBER, sectionNumber);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    logger = Logger.getInstance();
    return inflater.inflate(R.layout.control_fragment, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setUpUi(getView());
    logger.logForAndroid("UI is set up");
    setUpBluetooth();
    logger.logForAndroid("Bluetooth is set up");
  }

  private void setUpUi(View view) {
    setUpButtons(view);
    setUpMiscUi(view);
    setUpSpinner();
  }

  private void setUpBluetooth() {
    BluetoothHandler handler = BluetoothHandler.getInstance();
    handler.addLogReaderObserver(this);
    handler.addOberver(this);

  }

  private void setUpSpinner() {
    ArrayList<String> strategyList = new ArrayList<>();
    for (Strategies strategy : Strategies.values()) {
      strategyList.add(strategy.name());
    }

    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
        android.R.layout.simple_spinner_dropdown_item, strategyList);
    strategyChooser.setAdapter(dataAdapter);
  }

  private void showUpdatedBluetoothState(BluetoothState state) {
    switch (state) {
      case NOT_CONNECTED:
        bluetoothStateView.setText("Not connected");
        connect.setText("Connect");
        break;
      case CONNECTED:
        bluetoothStateView.setText("Connected");
        connect.setText("Disconnect");
        break;
      case CONNECTING:
        bluetoothStateView.setText("Connecting");
        connect.setText("Wait");
        break;
      default:
        bluetoothStateView.setText("Not defined");
        connect.setText("Not sure");
        logger.logForAndroid("Undefined Bluetoothstate in mainactivity");
        break;
    }
  }


  public Strategies getStrategy() {
    int position = strategyChooser.getSelectedItemPosition();
    return Strategies.values()[position];
  }

  public Command getStrategyCommand() {
    int param = StrategyHelper.intFromStrategy(getStrategy());
    return new Command(CommandTypes.SET_STRATEGY, param);
  }

  private void setUpMiscUi(View view) {
    bluetoothStateView = view.findViewById(R.id.bluetooth_state);
    strategyChooser = view.findViewById(R.id.chooseStrategy);
    speedChooser = view.findViewById(R.id.speedChooser);
    speedChooser.setOnSeekBarChangeListener(new OnSpeedChangeListener());
    autoSwitchToPid = view.findViewById(R.id.switchtopid);
    autoSwitchToPid.setOnCheckedChangeListener((buttonView, isChecked) -> handler
        .sendCommand(new Command(CommandTypes.SWITCH_TO_PID_AUTO, isChecked ? 1 : 0)));
  }

  private void setUpButtons(View view) {
    ImageButton forwardButton = view.findViewById(R.id.forward);
    forwardButton.setOnClickListener(v -> handler.sendDefaultCommand(CommandTypes.DRIVE_FORWARD));
    ImageButton backwardButton = view.findViewById(R.id.backward);
    backwardButton.setOnClickListener(v -> handler.sendDefaultCommand(CommandTypes.DRIVE_BACKWARD));
    ImageButton leftButton = view.findViewById(R.id.left);
    leftButton.setOnClickListener(v -> handler.sendDefaultCommand(CommandTypes.TURN_LEFT));
    ImageButton rightButton = view.findViewById(R.id.right);
    rightButton.setOnClickListener(v -> handler.sendDefaultCommand(CommandTypes.TURN_RIGHT));
    ImageButton setStrategy = view.findViewById(R.id.setStrategy);
    setStrategy.setOnClickListener(v ->
        handler.sendCommand(getStrategyCommand()));
    Button stopButton = view.findViewById(R.id.stop);
    stopButton.setOnClickListener(v -> handler.sendDefaultCommand(CommandTypes.STOP_ROBOT));
    Button overtakeButton = view.findViewById(R.id.overtake);
    overtakeButton.setOnClickListener(v -> handler.sendDefaultCommand(CommandTypes.OVERTAKE));
    connect = view.findViewById(R.id.connect);
    connect.setOnClickListener(new OnBluetoothConnectListener());
  }


  /**
   * If the BluetoothAdapter switches its state, it will notify the activity. The activity will then
   * display the new state
   */

  @Override
  public void update(BluetoothObservable observable, BluetoothState state) {
    logger.logForAndroid("Activity was notified");
    showUpdatedBluetoothState(state);
  }

  /**
   * Applies new state of robot.
   * Runs on UI Thread.
   * @param data new statedata
   */
  public void applyNewState(StateData data) {
    getActivity().runOnUiThread(() -> {
      switch (data.getType()) {
        case SPEED:
          speedChooser.setProgress(data.getData());
          logger.logForRobot("Updated speed of robot. "+data.getData());
          break;
        case STRATEGY:
          strategyChooser.setSelection(data.getData(), true);
         // logger.logForAndroid("Updated strategy of robot.");
          break;
        case EXPECTED_LIGHT_VALUE:
          logger.logForRobot("Expected Light Value of Robot:" + data.getData());
          break;
        case SWITCH_AUTO_TO_PID:
          boolean result = false;
          if (data.getData() == 1) {
            result = true;
          }
          autoSwitchToPid.setChecked(result);
          logger.logForRobot("Set SwitchToAutoPid to " + result);
          break;
        default:
          logger.logForAndroid("Unknown StateDataType");
      }
    });

  }


}


