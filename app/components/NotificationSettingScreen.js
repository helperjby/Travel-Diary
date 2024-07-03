import React, { useState } from 'react';
import { View, StyleSheet, Text } from 'react-native';
import ToggleSwitch from 'toggle-switch-react-native';

const NotificationSettingScreen = () => {
  const [diaryNotification, setDiaryNotification] = useState(true);
  const [emailNotification, setEmailNotification] = useState(true);
  const [soundNotification, setSoundNotification] = useState(true);
  const [pushNotification, setPushNotification] = useState(true);

  const toggleDiaryNotification = () => setDiaryNotification((prevState) => !prevState);
  const toggleEmailNotification = () => setEmailNotification((prevState) => !prevState);
  const toggleSoundNotification = () => setSoundNotification((prevState) => !prevState);
  const togglePushNotification = () => setPushNotification((prevState) => !prevState);

  return (
    <View style={styles.container}>
      <View style={styles.settingRow}>
        <Text style={styles.settingLabel}>다이어리 작성 알림</Text>
        <ToggleSwitch
          isOn={diaryNotification}
          onColor="#007AFF"
          offColor="#D0D0D0"
          size="medium"
          onToggle={toggleDiaryNotification}
        />
      </View>
      <View style={styles.settingRow}>
        <Text style={styles.settingLabel}>댓글 알림</Text>
        <ToggleSwitch
          isOn={emailNotification}
          onColor="#007AFF"
          offColor="#D0D0D0"
          size="medium"
          onToggle={toggleEmailNotification}
        />
      </View>
      <View style={styles.settingRow}>
        <Text style={styles.settingLabel}>좋아요 알림</Text>
        <ToggleSwitch
          isOn={soundNotification}
          onColor="#007AFF"
          offColor="#D0D0D0"
          size="medium"
          onToggle={toggleSoundNotification}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 16,
    paddingVertical: 24,
  },
  settingRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
  },
  settingLabel: {
    fontSize: 16,
    color: '#000000',
  },
});

export default NotificationSettingScreen;
