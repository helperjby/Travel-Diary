import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Octicons, MaterialIcons, MaterialCommunityIcons, FontAwesome6 } from '@expo/vector-icons';
// import { useSelector } from 'react-redux';

const ProfileScreen = ({ navigation }) => {
    //const user = useSelector((state) => state.auth.user);

    return (
        <View style={styles.container}>
            <Text style={styles.title}>안녕하세요! 닉네임님</Text>
            <TouchableOpacity onPress={() => navigation.navigate('ProfileEdit')} style={styles.buttonContainer}>
                <View style={styles.buttonContent}>
                    <Octicons name="person" size={24} color="black" />
                    <View style={styles.textContainer}>
                        <Text>프로필 변경</Text>
                    </View>
                </View>
                <FontAwesome6 name="angle-right" size={24} color="black" />
            </TouchableOpacity>
            <TouchableOpacity onPress={() => navigation.navigate('NotificationSetting')} style={styles.buttonContainer}>
                <View style={styles.buttonContent}>
                    <MaterialIcons name="notifications-none" size={24} color="black" />
                    <View style={styles.textContainer}>
                        <Text>알림 설정</Text>
                    </View>
                </View>
                <FontAwesome6 name="angle-right" size={24} color="black" />
            </TouchableOpacity>
            <TouchableOpacity onPress={() => navigation.navigate('FAQ')} style={styles.buttonContainer}>
                <View style={styles.buttonContent}>
                    <MaterialCommunityIcons name="message-processing-outline" size={24} color="black" />
                    <View style={styles.textContainer}>
                        <Text>FAQ</Text>
                    </View>
                </View>
                <FontAwesome6 name="angle-right" size={24} color="black" />
            </TouchableOpacity>
            <TouchableOpacity onPress={() => navigation.navigate('ContactAdmin')} style={styles.buttonContainer}>
                <View style={styles.buttonContent}>
                    <Octicons name="pencil" size={24} color="black" />
                    <View style={styles.textContainer}>
                        <Text>관리자 문의</Text>
                    </View>
                </View>
                <FontAwesome6 name="angle-right" size={24} color="black" />
            </TouchableOpacity>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
        backgroundColor: 'white',
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 40,
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 30,
    },
    buttonContent: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    textContainer: {
        marginLeft: 8,
        flex: 1,
    },
});

export default ProfileScreen;