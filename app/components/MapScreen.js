import React, { useState, useEffect, useRef } from 'react';
import { View, StyleSheet, Text, TextInput, BackHandler, Alert, Dimensions, Platform } from 'react-native';
import MapView, { Marker, Polyline, PROVIDER_GOOGLE } from 'react-native-maps';
import * as Location from 'expo-location';
import MapBottomSheetScreen from './MapBottomSheetScreen';
import moment from 'moment';
import Header1 from './Header1'; // 헤더 컴포넌트 추가

const SCREEN_HEIGHT = Dimensions.get('window').height;
const MapScreen = ({ navigation }) => {
  const [location, setLocation] = useState(null);
  const [errorMsg, setErrorMsg] = useState(null);
  const [markers, setMarkers] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchedLocation, setSearchedLocation] = useState(null);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [selectedMarkerIndex, setSelectedMarkerIndex] = useState(-1);
  const [selectedLocationAddress, setSelectedLocationAddress] = useState('');
  const [selectedDate, setSelectedDate] = useState(moment().format('YYYY-MM-DD'));
  const currentDate = moment().format('YYYY-MM-DD');
  const mapRef = useRef(null);
  const [bottomSheetTranslateY, setBottomSheetTranslateY] = useState(SCREEN_HEIGHT * 0.9);

  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      if (searchedLocation) {
        setSearchedLocation(null);
        setSearchQuery('');
        return true;
      }
      return false;
    });

    return () => backHandler.remove();
  }, [searchedLocation]);

  useEffect(() => {
    const fetchLocation = async () => {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') {
        setErrorMsg('위치 접근 권한이 거부되었습니다.');
        return;
      }

      try {
        const locationData = await Location.getCurrentPositionAsync({});
        setLocation(locationData);
      } catch (error) {
        console.error('현재 위치를 가져오는 중 오류 발생:', error);
        setErrorMsg('위치를 가져오는 데 실패했습니다.');
        setShowErrorMessage(true);

        setTimeout(() => {
          setShowErrorMessage(false);
        }, 5000);
      }
    };

    fetchLocation();
  }, []);

  const handleMapPress = async (event) => {
    if (selectedDate !== currentDate) {
      Alert.alert('현재 날짜에만 마커를 추가할 수 있습니다.');
      return;
    }

    const { latitude, longitude } = event.nativeEvent.coordinate;
    const locationName = await getLocationName(latitude, longitude);
    const newMarker = { latitude, longitude, date: selectedDate, name: locationName };

    setMarkers([...markers, newMarker]);
  };

  const getLocationName = async (latitude, longitude) => {
    try {
      const locationDetails = await Location.reverseGeocodeAsync({ latitude, longitude });
      if (locationDetails && locationDetails.length > 0) {
        const { country, region, city, street, name, postalCode } = locationDetails[0];
        let address = '';
        if (country) address += country;
        if (region) address += ` ${region}`;
        if (city) address += ` ${city}`;
        if (street) address += ` ${street}`;
        if (name) address += ` ${name}`;
        if (postalCode) address += ` ${postalCode}`;
        return address || '위치 정보 없음';
      } else {
        return '위치 정보 없음';
      }
    } catch (error) {
      console.error('위치 세부 정보를 가져오는 중 오류 발생:', error);
      return '위치 정보 없음';
    }
  };

  const handleMarkerPress = (index, deleteMarker = false) => {
    if (deleteMarker) {
      deleteMarkerByIndex(index);
    } else {
      setSelectedMarkerIndex(index);
      const marker = markers[index];

      let offset = 0.5; // Default offset for MIN_TRANSLATE_Y
      if (bottomSheetTranslateY === SCREEN_HEIGHT * 0.9) {
        offset = 0.05; // Offset for COLLAPSED_TRANSLATE_Y
      } else if (bottomSheetTranslateY === SCREEN_HEIGHT * 0.2) {
        offset = 0.8; // Offset for MAX_TRANSLATE_Y
      }

      mapRef.current.animateToRegion({
        latitude: marker.latitude - (SCREEN_HEIGHT * offset) / 100000,
        longitude: marker.longitude,
        latitudeDelta: 0.0122,
        longitudeDelta: 0.0121,
      }, 1000);
    }
  };

  const deleteMarkerByIndex = (index) => {
    const updatedMarkers = markers.filter((marker, idx) => idx !== index);
    setMarkers(updatedMarkers);
    setSelectedMarkerIndex(-1);
  };

  const handleSearch = async () => {
    if (!searchQuery) return;

    try {
      const searchLocation = await Location.geocodeAsync(searchQuery);
      if (searchLocation && searchLocation.length > 0) {
        const { latitude, longitude } = searchLocation[0];
        setSearchedLocation({ latitude, longitude });
      } else {
        setErrorMsg('위치를 찾을 수 없습니다.');
        setShowErrorMessage(true);

        setTimeout(() => {
          setShowErrorMessage(false);
        }, 5000);
      }
    } catch (error) {
      console.error('위치를 검색하는 중 오류 발생:', error);
      setErrorMsg('위치 검색에 실패했습니다.');
      setShowErrorMessage(true);

      setTimeout(() => {
        setShowErrorMessage(false);
      }, 5000);
    }
  };

  const getLocationDetails = async (latitude, longitude) => {
    try {
      const locationDetails = await Location.reverseGeocodeAsync({ latitude, longitude });
      if (locationDetails && locationDetails.length > 0) {
        const { country, region, city, street, name, postalCode } = locationDetails[0];
        let address = '';
        if (country) address += country;
        if (region) address += ` ${region}`;
        if (city) address += ` ${city}`;
        if (street) address += ` ${street}`;
        if (name) address += ` ${name}`;
        if (postalCode) address += ` ${postalCode}`;
        setSelectedLocationAddress(address);
      } else {
        setSelectedLocationAddress('');
      }
    } catch (error) {
      console.error('위치 세부 정보를 가져오는 중 오류 발생:', error);
      setSelectedLocationAddress('');
    }
  };

  const filteredMarkers = markers.filter(marker => marker.date === selectedDate);

  const polylineCoordinates = filteredMarkers.map(marker => ({
    latitude: marker.latitude,
    longitude: marker.longitude,
  }));

  const initialRegion = {
    latitude: location ? location.coords.latitude : 37.5665,
    longitude: location ? location.coords.longitude : 126.9780,
    latitudeDelta: 0.0122,
    longitudeDelta: 0.0121,
  };

  const region = searchedLocation
    ? {
        latitude: searchedLocation.latitude,
        longitude: searchedLocation.longitude,
        latitudeDelta: 0.0122,
        longitudeDelta: 0.0121,
      }
    : initialRegion;

  return (
    <View style={styles.container}>
      <Header1 title="지도" />
      <TextInput
        style={styles.input}
        placeholder="위치 검색..."
        onChangeText={(text) => setSearchQuery(text)}
        value={searchQuery}
        onSubmitEditing={handleSearch}
      />
      <MapView
        ref={mapRef}
        style={styles.map}
        region={region}
        provider={PROVIDER_GOOGLE}
        showsUserLocation={true}
        onPress={handleMapPress}
      >
        {location && !searchedLocation && (
          <Marker
            coordinate={{
              latitude: location.coords.latitude,
              longitude: location.coords.longitude,
            }}
            title="내 위치"
            description="초기 위치 마커"
            pinColor="blue"
          />
        )}
        {filteredMarkers.map((marker, index) => (
          <Marker
            key={index}
            coordinate={{
              latitude: marker.latitude,
              longitude: marker.longitude,
            }}
            onPress={() => handleMarkerPress(index)}
          >
            <View style={styles.customMarker}>
              <Text style={styles.markerText}>{index + 1}</Text>
            </View>
          </Marker>
        ))}
        <Polyline
          coordinates={polylineCoordinates}
          strokeColor="#3498db"
          strokeWidth={2}
        />
      </MapView>
      <MapBottomSheetScreen
        markers={filteredMarkers}
        selectedDate={selectedDate}
        setSelectedDate={setSelectedDate}
        onMarkerPress={handleMarkerPress}
        getTranslateY={setBottomSheetTranslateY}
      />
      {showErrorMessage && (
        <View style={styles.errorContainer}>
          <Text style={styles.errorText}>{errorMsg}</Text>
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  map: {
    flex: 1,
  },
  input: {
    height: 40,
    borderRadius: 20,
    paddingHorizontal: 16,
    marginHorizontal: 16,
    marginTop: 8,
    marginBottom: 8,
    backgroundColor: 'white',
  },
  customMarker: {
    width: 30,
    height: 30,
    backgroundColor: 'black',
    borderRadius: 15,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 5,
  },
  markerText: {
    fontWeight: 'bold',
    color: 'white',
    textAlign: 'center',
  },
  errorContainer: {
    position: 'absolute',
    bottom: 16,
    left: 0,
    right: 0,
    backgroundColor: 'red',
    padding: 10,
  },
  errorText: {
    color: 'white',
    textAlign: 'center',
  },
});

export default MapScreen;
