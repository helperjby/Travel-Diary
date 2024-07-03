import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Feather } from '@expo/vector-icons';

const ContactAdminScreen = () => {
    return (
        <View style={styles.container}>
            <View style={styles.writeContainer}>
                <Text style={styles.write}>
                    불편을 드려 죄송합니다.
                </Text>
                <Text style={styles.write}>
                    문의가 필요한 경우 다음 주소로 연락바랍니다.
                </Text>
            </View>
            <View style={styles.content}>
                <Feather name="mail" size={24} color="black" style={styles.icon} />
                <View style={styles.textContainer}>
                    <Text style={styles.mail}>abcde@gmail.com</Text>
                    {/* 관리자 메일 추후 변경 */}
                </View>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: 'white',
    },
    write: {
        fontSize: 22,
        fontWeight: 'bold',
        marginBottom: 10,
        textAlign: 'center',
    },
    writeContainer: {
        marginBottom: 40,
    },
    content: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    icon: {
        marginRight: 10,
    },
    mail: {
        fontSize: 16,
    },
});

export default ContactAdminScreen;
