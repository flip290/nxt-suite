package de.brick6.nxtremote.ui;

import android.widget.SeekBar;
import messaging.Command;
import messaging.CommandTypes;

public  class OnSpeedChangeListener implements SeekBar.OnSeekBarChangeListener {
  private CommandHandler handler = new CommandHandler();


  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    //nuthin
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    //nuthin
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    handler.sendCommand(new Command(CommandTypes.SET_SPEED,seekBar.getProgress()));
  }




}
