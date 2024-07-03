import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, FlatList, StyleSheet, Dimensions } from 'react-native';
import Animated, { useSharedValue, withSpring, useAnimatedStyle } from 'react-native-reanimated';
import { Gesture, GestureDetector } from 'react-native-gesture-handler';
import DateTimePickerModal from 'react-native-modal-datetime-picker';
import moment from 'moment';

const SCREEN_HEIGHT = Dimensions.get('window').height;
const COLLAPSED_TRANSLATE_Y = SCREEN_HEIGHT * 0.9; // Bottom sheet height when collapsed (10% of screen)
const MIN_TRANSLATE_Y = SCREEN_HEIGHT * 0.6; // Bottom sheet height when minimized (2/5 of screen)
const MAX_TRANSLATE_Y = SCREEN_HEIGHT * 0.2; // Bottom sheet height when maximized (4/5 of screen)

const MapBottomSheetScreen = ({ markers, selectedDate, setSelectedDate, onMarkerPress, getTranslateY }) => {
  const translateY = useSharedValue(COLLAPSED_TRANSLATE_Y);
  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);
  const [selectedMarkerIndex, setSelectedMarkerIndex] = useState(-1);

  useEffect(() => {
    getTranslateY(translateY.value);
  }, [translateY.value, getTranslateY]);

  const showDatePicker = () => {
    setDatePickerVisibility(true);
  };

  const hideDatePicker = () => {
    setDatePickerVisibility(false);
  };

  const handleConfirm = (date) => {
    const formattedDate = moment(date).format('YYYY-MM-DD');
    setSelectedDate(formattedDate);
    hideDatePicker();
  };

  const animatedStyle = useAnimatedStyle(() => {
    return { transform: [{ translateY: translateY.value }] };
  });

  const gesture = Gesture.Pan()
    .onUpdate((event) => {
      translateY.value = Math.max(MAX_TRANSLATE_Y, Math.min(COLLAPSED_TRANSLATE_Y, translateY.value + event.translationY));
    })
    .onEnd(() => {
      if (translateY.value > SCREEN_HEIGHT * 0.75) {
        translateY.value = withSpring(COLLAPSED_TRANSLATE_Y);
      } else if (translateY.value > SCREEN_HEIGHT * 0.4) {
        translateY.value = withSpring(MIN_TRANSLATE_Y);
      } else {
        translateY.value = withSpring(MAX_TRANSLATE_Y);
      }
      getTranslateY(translateY.value);
    });

  const handleTap = () => {
    if (translateY.value === COLLAPSED_TRANSLATE_Y) {
      translateY.value = withSpring(MIN_TRANSLATE_Y);
    } else if (translateY.value === MIN_TRANSLATE_Y) {
      translateY.value = withSpring(MAX_TRANSLATE_Y);
    } else {
      translateY.value = withSpring(COLLAPSED_TRANSLATE_Y);
    }
    getTranslateY(translateY.value);
  };

  const handleMarkerPressInBottomSheet = (index) => {
    setSelectedMarkerIndex(index);
    onMarkerPress(index);
  };

  const handleDeleteMarker = (index) => {
    onMarkerPress(index, true);
    setSelectedMarkerIndex(-1);
  };

  return (
    <GestureDetector gesture={gesture}>
      <Animated.View style={[styles.container, animatedStyle]}>
        <TouchableOpacity onPress={handleTap}>
          <View style={styles.line} />
        </TouchableOpacity>
        <TouchableOpacity onPress={showDatePicker}>
          <Text style={styles.dateText}>{selectedDate} ▼</Text>
        </TouchableOpacity>
        <DateTimePickerModal
          isVisible={isDatePickerVisible}
          mode="date"
          onConfirm={handleConfirm}
          onCancel={hideDatePicker}
        />
        <FlatList
          data={markers}
          keyExtractor={(item, index) => index.toString()}
          renderItem={({ item, index }) => (
            <View style={styles.markerItem}>
              <TouchableOpacity style={{ flex: 1 }} onPress={() => handleMarkerPressInBottomSheet(index)}>
                <View style={styles.markerInfo}>
                  <View style={styles.customMarker}>
                    <Text style={styles.markerText}>{index + 1}</Text>
                  </View>
                  <Text style={styles.markerInfoText}>{item.name}</Text>
                </View>
              </TouchableOpacity>
              {selectedMarkerIndex === index && (
                <TouchableOpacity style={styles.deleteButton} onPress={() => handleDeleteMarker(index)}>
                  <Text style={styles.deleteButtonText}>삭제</Text>
                </TouchableOpacity>
              )}
            </View>
          )}
        />
      </Animated.View>
    </GestureDetector>
  );
};

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    bottom: 0,
    width: '100%',
    height: SCREEN_HEIGHT,
    backgroundColor: 'white',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: -3 },
    shadowOpacity: 0.1,
    shadowRadius: 5,
    elevation: 5,
  },
  line: {
    width: 40,
    height: 5,
    backgroundColor: '#ccc',
    borderRadius: 2.5,
    alignSelf: 'center',
    marginVertical: 10,
  },
  dateText: {
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 10,
  },
  markerItem: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  markerInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  customMarker: {
    width: 30,
    height: 30,
    backgroundColor: 'black',
    borderRadius: 15,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 10,
  },
  markerText: {
    fontWeight: 'bold',
    color: 'white',
    textAlign: 'center',
  },
  markerInfoText: {
    fontSize: 16,
  },
  deleteButton: {
    backgroundColor: 'white',
    borderColor: 'gray',
    borderWidth: 1,
    borderRadius: 5,
    paddingVertical: 5,
    paddingHorizontal: 10,
    marginLeft: 10,
  },
  deleteButtonText: {
    color: 'black',
  },
});

export default MapBottomSheetScreen;
