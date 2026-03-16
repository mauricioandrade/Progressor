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
                signup_link: "Sign Up",
                errors: {
                    invalid_credentials: "Invalid email or password.",
                    server_error: "Connection failed. Try again later.",
                    unexpected: "An unexpected error occurred."
                },
                success: "Login successful!"
            },
            signup: {
                title: "Create Account",
                subtitle: "Choose your profile and start today.",
                name_label: "Full Name",
                email_label: "Email",
                password_label: "Password",
                birthdate_label: "Birth Date",
                cref_label: "CREF",
                crn_label: "CRN",
                role_student: "Student",
                role_personal: "Personal Trainer",
                role_nutritionist: "Nutritionist",
                button: "Register",
                have_account: "Already have an account?",
                login_link: "Sign In",
                success: "Registration successful!"
            },
            sidebar: {
                dashboard: "Dashboard",
                my_workouts: "My Workouts",
                create_workout: "Create Workout",
                students: "My Students",
                measurements: "Measurements",
                reports: "Reports",
                settings: "Settings"
            },
            students_list: {
                title: "My Students",
                name: "Name",
                email: "Email",
                actions: "Actions",
                view_profile: "View Profile",
                no_students: "No students found."
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
                signup_link: "Cadastre-se",
                errors: {
                    invalid_credentials: "E-mail ou senha inválidos.",
                    server_error: "Falha na conexão. Tente novamente mais tarde.",
                    unexpected: "Ocorreu um erro inesperado."
                },
                success: "Login realizado com sucesso!"
            },
            signup: {
                title: "Criar Conta",
                subtitle: "Escolha seu perfil e comece hoje mesmo.",
                name_label: "Nome Completo",
                email_label: "E-mail",
                password_label: "Senha",
                birthdate_label: "Data de Nascimento",
                cref_label: "CREF",
                crn_label: "CRN",
                role_student: "Aluno",
                role_personal: "Personal Trainer",
                role_nutritionist: "Nutricionista",
                button: "Cadastrar",
                have_account: "Já tem uma conta?",
                login_link: "Entrar",
                success: "Cadastro realizado com sucesso!"
            },
            sidebar: {
                dashboard: "Dashboard",
                my_workouts: "Meus Treinos",
                create_workout: "Criar Treino",
                students: "Meus Alunos",
                measurements: "Avaliações",
                reports: "Relatórios",
                settings: "Configurações"
            },
            students_list: {
                title: "Meus Alunos",
                name: "Nome",
                email: "E-mail",
                actions: "Ações",
                view_profile: "Ver Perfil",
                no_students: "Nenhum aluno encontrado."
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