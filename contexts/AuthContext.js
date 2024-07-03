import React, { createContext, useState, useContext } from 'react';
import * as AuthSession from 'expo-auth-session';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const login = async (authConfig) => {
        setIsLoading(true);
        try {
            const authResult = await AuthSession.startAsync(authConfig);
            if (authResult.type === 'success') {
                setUser(authResult.user);
            }
        } catch (error) {
            console.error('로그인 에러:', error);
        } finally {
            setIsLoading(false);
        }
    };

    const logout = () => {
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
