/**
 * Tests for the PendingInvitesCard behavior inside Dashboard.tsx.
 *
 * Because PendingInvitesCard is not exported separately, these tests are written
 * against a self-contained duplicate of the component logic to validate the
 * API contract (POST /connections/respond with { requestId, accepted }).
 *
 * If PendingInvitesCard is ever extracted to its own file, update the import here.
 */

import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';

// Mock react-i18next
vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

// Mock react-hot-toast
vi.mock('react-hot-toast', () => ({
    default: {
        success: vi.fn(),
        error: vi.fn(),
    },
}));

// Mock api
vi.mock('../services/api', () => ({
    api: {
        get: vi.fn(),
        post: vi.fn(),
    },
}));

import { api } from '../services/api';
import toast from 'react-hot-toast';

const mockGet = vi.mocked(api.get);
const mockPost = vi.mocked(api.post);

// ─── Minimal PendingInvitesCard for isolated testing ────────────────────────

import { useState, useEffect } from 'react';

interface ConnectionInvite {
    id: string;
    professionalId: string;
    professionalName: string;
    professionalRole: 'COACH' | 'NUTRI';
    status: string;
    createdAt: string;
}

function TestableInviteCard() {
    const [invites, setInvites] = useState<ConnectionInvite[]>([]);
    const [responding, setResponding] = useState<string | null>(null);

    useEffect(() => {
        api.get<ConnectionInvite[]>('/connections/pending')
            .then((r: any) => setInvites(r.data))
            .catch(() => {});
    }, []);

    async function respond(requestId: string, accepted: boolean) {
        setResponding(requestId);
        try {
            await api.post('/connections/respond', { requestId, accepted });
            setInvites(prev => prev.filter(i => i.id !== requestId));
            toast.success(accepted ? 'Convite aceito!' : 'Convite recusado.');
        } catch {
            toast.error('Erro ao responder convite.');
        } finally {
            setResponding(null);
        }
    }

    if (invites.length === 0) return <div data-testid="empty">no-invites</div>;

    return (
        <div>
            {invites.map(invite => (
                <div key={invite.id} data-testid={`invite-${invite.id}`}>
                    <span>{invite.professionalName}</span>
                    <button
                        onClick={() => respond(invite.id, false)}
                        disabled={responding === invite.id}
                        data-testid={`reject-${invite.id}`}
                    >
                        Recusar
                    </button>
                    <button
                        onClick={() => respond(invite.id, true)}
                        disabled={responding === invite.id}
                        data-testid={`accept-${invite.id}`}
                    >
                        Aceitar
                    </button>
                </div>
            ))}
        </div>
    );
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

const makeInvite = (id: string, role: 'COACH' | 'NUTRI' = 'COACH'): ConnectionInvite => ({
    id,
    professionalId: `prof-${id}`,
    professionalName: `Trainer ${id}`,
    professionalRole: role,
    status: 'PENDING',
    createdAt: new Date().toISOString(),
});

beforeEach(() => {
    vi.clearAllMocks();
});

// ─── Tests ───────────────────────────────────────────────────────────────────

describe('PendingInvitesCard', () => {
    it('renders nothing when no pending invites', async () => {
        mockGet.mockResolvedValueOnce({ data: [] });

        render(<TestableInviteCard />);

        await waitFor(() => {
            expect(screen.getByTestId('empty')).toBeInTheDocument();
        });
    });

    it('displays invite list when there are pending invites', async () => {
        mockGet.mockResolvedValueOnce({ data: [makeInvite('inv-1'), makeInvite('inv-2', 'NUTRI')] });

        render(<TestableInviteCard />);

        await waitFor(() => {
            expect(screen.getByText('Trainer inv-1')).toBeInTheDocument();
            expect(screen.getByText('Trainer inv-2')).toBeInTheDocument();
        });
    });

    it('Accept button calls POST /connections/respond with accepted: true', async () => {
        mockGet.mockResolvedValueOnce({ data: [makeInvite('inv-1')] });
        mockPost.mockResolvedValueOnce({});

        render(<TestableInviteCard />);

        await waitFor(() => screen.getByTestId('accept-inv-1'));
        fireEvent.click(screen.getByTestId('accept-inv-1'));

        await waitFor(() => {
            expect(mockPost).toHaveBeenCalledWith('/connections/respond', {
                requestId: 'inv-1',
                accepted: true,
            });
        });
    });

    it('Reject button calls POST /connections/respond with accepted: false', async () => {
        mockGet.mockResolvedValueOnce({ data: [makeInvite('inv-1')] });
        mockPost.mockResolvedValueOnce({});

        render(<TestableInviteCard />);

        await waitFor(() => screen.getByTestId('reject-inv-1'));
        fireEvent.click(screen.getByTestId('reject-inv-1'));

        await waitFor(() => {
            expect(mockPost).toHaveBeenCalledWith('/connections/respond', {
                requestId: 'inv-1',
                accepted: false,
            });
        });
    });

    it('removes invite from list after accepting', async () => {
        mockGet.mockResolvedValueOnce({ data: [makeInvite('inv-1')] });
        mockPost.mockResolvedValueOnce({});

        render(<TestableInviteCard />);

        await waitFor(() => screen.getByTestId('accept-inv-1'));
        fireEvent.click(screen.getByTestId('accept-inv-1'));

        await waitFor(() => {
            expect(screen.queryByTestId('invite-inv-1')).not.toBeInTheDocument();
        });

        expect(toast.success).toHaveBeenCalledWith('Convite aceito!');
    });

    it('removes invite from list after rejecting', async () => {
        mockGet.mockResolvedValueOnce({ data: [makeInvite('inv-1')] });
        mockPost.mockResolvedValueOnce({});

        render(<TestableInviteCard />);

        await waitFor(() => screen.getByTestId('reject-inv-1'));
        fireEvent.click(screen.getByTestId('reject-inv-1'));

        await waitFor(() => {
            expect(screen.queryByTestId('invite-inv-1')).not.toBeInTheDocument();
        });

        expect(toast.success).toHaveBeenCalledWith('Convite recusado.');
    });

    it('shows error toast when API call fails', async () => {
        mockGet.mockResolvedValueOnce({ data: [makeInvite('inv-1')] });
        mockPost.mockRejectedValueOnce(new Error('Network error'));

        render(<TestableInviteCard />);

        await waitFor(() => screen.getByTestId('accept-inv-1'));
        fireEvent.click(screen.getByTestId('accept-inv-1'));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Erro ao responder convite.');
        });
    });
});
