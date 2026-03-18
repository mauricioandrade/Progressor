import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { WorkoutSpreadsheetView } from './WorkoutSpreadsheetView';

// Mock react-i18next
vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

// Mock api
vi.mock('../services/api', () => ({
    api: {
        get: vi.fn(),
    },
}));

import { api } from '../services/api';
const mockGet = vi.mocked(api.get);

const makePlan = (overrides = {}) => ({
    id: 'plan-1',
    name: 'Plano Força',
    createdAt: '2026-01-01T00:00:00',
    blocks: [
        {
            id: 'block-1',
            name: 'Bloco A – Superior',
            position: 0,
            exercises: [
                {
                    id: 'ex-1',
                    name: 'Supino Reto',
                    sets: 4,
                    repetitions: 8,
                    weightInKg: 80,
                    measurementType: 'WEIGHT',
                },
            ],
        },
    ],
    ...overrides,
});

beforeEach(() => {
    vi.clearAllMocks();
});

describe('WorkoutSpreadsheetView', () => {
    it('shows empty state when no plans exist', async () => {
        mockGet.mockResolvedValueOnce({ data: [] });

        render(<WorkoutSpreadsheetView />);

        await waitFor(() => {
            expect(screen.getByText(/Nenhum plano de treino criado ainda/i)).toBeInTheDocument();
        });
    });

    it('renders plan name and block header after loading', async () => {
        mockGet
            .mockResolvedValueOnce({ data: [makePlan()] })   // plans
            .mockResolvedValueOnce({ data: [] });              // history for ex-1

        render(<WorkoutSpreadsheetView />);

        await waitFor(() => {
            expect(screen.getByText('Plano Força')).toBeInTheDocument();
        });

        // Block header should be visible (plan expanded by default)
        expect(screen.getByText(/Bloco A – Superior/i)).toBeInTheDocument();
    });

    it('renders exercise row with sets × reps and weight', async () => {
        mockGet
            .mockResolvedValueOnce({ data: [makePlan()] })
            .mockResolvedValueOnce({ data: [] });

        render(<WorkoutSpreadsheetView />);

        await waitFor(() => {
            expect(screen.getByText('Supino Reto')).toBeInTheDocument();
        });

        expect(screen.getByText('4 × 8')).toBeInTheDocument();
        expect(screen.getByText('80kg')).toBeInTheDocument();
    });

    it('renders history logs in week columns', async () => {
        const logs = [
            { id: 'log-1', exerciseId: 'ex-1', actualWeight: 75, actualReps: 8, completedAt: '2026-01-06T10:00:00', tonnageAchieved: 600 },
            { id: 'log-2', exerciseId: 'ex-1', actualWeight: 77.5, actualReps: 8, completedAt: '2026-01-13T10:00:00', tonnageAchieved: 620 },
        ];

        mockGet
            .mockResolvedValueOnce({ data: [makePlan()] })
            .mockResolvedValueOnce({ data: logs });

        render(<WorkoutSpreadsheetView />);

        await waitFor(() => {
            expect(screen.getByText('75kg')).toBeInTheDocument();
        });

        expect(screen.getByText('77.5kg')).toBeInTheDocument();
    });

    it('does not crash when blocks is null or undefined', async () => {
        const planWithNullBlocks = {
            id: 'plan-null',
            name: 'Plano Sem Blocos',
            createdAt: '2026-01-01T00:00:00',
            blocks: null as any,
        };

        mockGet.mockResolvedValueOnce({ data: [planWithNullBlocks] });

        expect(() => render(<WorkoutSpreadsheetView />)).not.toThrow();

        await waitFor(() => {
            expect(screen.getByText('Plano Sem Blocos')).toBeInTheDocument();
        });
    });

    it('does not crash when exercises is null or undefined inside a block', async () => {
        const planWithNullExercises = makePlan({
            blocks: [
                { id: 'b1', name: 'Bloco X', position: 0, exercises: null as any },
            ],
        });

        mockGet.mockResolvedValueOnce({ data: [planWithNullExercises] });

        expect(() => render(<WorkoutSpreadsheetView />)).not.toThrow();

        await waitFor(() => {
            expect(screen.getByText(/Bloco X/i)).toBeInTheDocument();
        });
    });

    it('fetches plan using studentId when prop provided', async () => {
        mockGet.mockResolvedValueOnce({ data: [] });

        render(<WorkoutSpreadsheetView studentId="student-42" />);

        await waitFor(() => {
            expect(mockGet).toHaveBeenCalledWith('/workouts/plans/student/student-42');
        });
    });

    it('fetches own plans when no studentId', async () => {
        mockGet.mockResolvedValueOnce({ data: [] });

        render(<WorkoutSpreadsheetView />);

        await waitFor(() => {
            expect(mockGet).toHaveBeenCalledWith('/workouts/plans/my');
        });
    });
});
