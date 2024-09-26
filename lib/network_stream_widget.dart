import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'utils.dart';

/// NetworkStreamWidget handles and displays streaming of network events
class NetworkStreamWidget extends StatelessWidget {
  /// Initialize NetworkStreamWidget with [key].
  const NetworkStreamWidget({Key? key}) : super(key: key);
  final _eventChannel = const EventChannel('platform_channel_events/connectivity');
  @override
  Widget build(BuildContext context) {
    final networkStream = _eventChannel
        .receiveBroadcastStream()
        .distinct()
        .map((event) => intToConnection(event as int));
    return StreamBuilder<Connection>(
      initialData: Connection.disconnected,
      stream: networkStream,
      builder: (context, snapshot) {
        final connection = snapshot.data ?? Connection.unknown;
        final message = getConnectionMessage(connection);
        final color = getConnectionColor(connection);
        return _NetworkStateWidget(message: message, color: color);
      },
    );
  }
}

class _NetworkStateWidget extends StatelessWidget {
  final String message;
  final Color color;

  const _NetworkStateWidget({required this.message, required this.color});

  @override
  Widget build(BuildContext context) {
    return AnimatedContainer(
      color: color,
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 15),
      duration: kThemeAnimationDuration,
      child: Text(
        message,
        textAlign: TextAlign.center,
        style: Theme.of(context).textTheme.bodyText2!.copyWith(
              color: Colors.white,
              fontWeight: FontWeight.w600,
            ),
      ),
    );
  }
}
