package dev.fluttercommunity.plus.androidintent;

import android.content.Intent;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;

/**
 * Plugin implementation that uses the new {@code io.flutter.embedding} package.
 *
 * <p>Instantiate this in an add to app scenario to gracefully handle activity and context changes.
 */
public final class AndroidIntentPlugin implements FlutterPlugin, ActivityAware, ActivityResultListener {
  private final IntentSender sender;
  private final MethodCallHandlerImpl impl;

  /**
   * Initialize this within the {@code #configureFlutterEngine} of a Flutter activity or fragment.
   *
   * <p>See {@code io.flutter.plugins.androidintentexample.MainActivity} for an example.
   */
  public AndroidIntentPlugin() {
    sender = new IntentSender(/*activity=*/ null, /*applicationContext=*/ null);
    impl = new MethodCallHandlerImpl(sender);
    sender.setMethodCallHandlerImpl(impl);
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    sender.setApplicationContext(binding.getApplicationContext());
    sender.setActivity(null);
    impl.startListening(binding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    sender.setApplicationContext(null);
    sender.setActivity(null);
    impl.stopListening();
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    sender.setActivity(binding.getActivity());
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivity() {
    sender.setActivity(null);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    impl.result(resultCode, data);

    return true;
  }
}
