import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

const resources = {
    en: {
        translation: {
            login: {
                title: "Progressor",
                subtitle: "Your fitness journey taken seriously.",
                email_label: "Email",
                password_label: "Password",
                button: "Sign In",
                no_account: "Don't have an account?",
                signup_link: "Sign Up"
            }
        }
    },
    pt: {
        translation: {
            login: {
                title: "Progressor",
                subtitle: "Sua jornada fitness levada a sério.",
                email_label: "E-mail",
                password_label: "Senha",
                button: "Entrar",
                no_account: "Não tem uma conta?",
                signup_link: "Cadastre-se"
            }
        }
    }
};

i18n
    .use(LanguageDetector) 
    .use(initReactI18next)
    .init({
        resources,
        fallbackLng: 'en', 
        interpolation: {
            escapeValue: false
        }
    });

export default i18n;