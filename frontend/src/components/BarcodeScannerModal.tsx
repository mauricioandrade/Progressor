import { useEffect, useRef, useState } from 'react';
import { BrowserMultiFormatReader } from '@zxing/browser';
import type { IScannerControls } from '@zxing/browser';
import { X, Loader2, ScanLine, AlertCircle } from 'lucide-react';
import { api } from '../services/api';

interface FoodResult {
    foodId: string;
    name: string;
    brandName?: string | null;
    caloriesKcal: number;
    proteinG: number;
    carbsG: number;
    fatG: number;
    description: string;
}

interface Props {
    onFound: (food: FoodResult) => void;
    onClose: () => void;
}

const MACRO_COLORS = {
    calories: 'text-emerald-500',
    protein: 'text-rose-500',
    carbs: 'text-blue-500',
    fat: 'text-amber-500',
};

export function BarcodeScannerModal({ onFound, onClose }: Props) {
    const videoRef = useRef<HTMLVideoElement>(null);
    const controlsRef = useRef<IScannerControls | null>(null);
    const [status, setStatus] = useState<'scanning' | 'loading' | 'found' | 'not_found' | 'error'>('scanning');
    const [found, setFound] = useState<FoodResult | null>(null);
    const [errorMsg, setErrorMsg] = useState('');
    const lastBarcodeRef = useRef('');

    useEffect(() => {
        const reader = new BrowserMultiFormatReader();

        reader.decodeFromVideoDevice(undefined, videoRef.current!, async (result) => {
            if (!result) return;

            const barcode = result.getText();
            if (barcode === lastBarcodeRef.current) return;
            lastBarcodeRef.current = barcode;

            setStatus('loading');

            try {
                const res = await api.get(`/nutrition/foods/barcode/${barcode}`);
                setFound(res.data);
                setStatus('found');
            } catch (e: unknown) {
                const status = (e as { response?: { status?: number } })?.response?.status;
                if (status === 404) {
                    setStatus('not_found');
                    // allow retry after 2s
                    setTimeout(() => {
                        lastBarcodeRef.current = '';
                        setStatus('scanning');
                    }, 2000);
                } else {
                    setErrorMsg('Erro ao buscar produto. Tente novamente.');
                    setStatus('error');
                }
            }
        }).then(controls => {
            controlsRef.current = controls;
        }).catch(() => {
            setErrorMsg('Não foi possível acessar a câmera.');
            setStatus('error');
        });

        return () => {
            controlsRef.current?.stop();
        };
    }, []);

    function handleAdd() {
        if (found) {
            onFound(found);
            onClose();
        }
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
            <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl w-full max-w-sm overflow-hidden">
                {/* Header */}
                <div className="flex items-center justify-between px-5 py-4 border-b border-black/5 dark:border-white/10">
                    <div className="flex items-center gap-2">
                        <ScanLine className="w-5 h-5 text-blue-500" />
                        <span className="font-semibold text-gray-800 dark:text-gray-100 text-sm">
                            Escanear código de barras
                        </span>
                    </div>
                    <button
                        onClick={onClose}
                        className="w-8 h-8 flex items-center justify-center rounded-full hover:bg-black/5 dark:hover:bg-white/10 transition-colors"
                    >
                        <X className="w-4 h-4 text-gray-500" />
                    </button>
                </div>

                {/* Camera */}
                <div className="relative bg-black aspect-[4/3]">
                    <video ref={videoRef} className="w-full h-full object-cover" />

                    {/* Scan frame overlay */}
                    <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
                        <div className="w-56 h-32 relative">
                            <div className="absolute top-0 left-0 w-6 h-6 border-t-2 border-l-2 border-white rounded-tl-lg" />
                            <div className="absolute top-0 right-0 w-6 h-6 border-t-2 border-r-2 border-white rounded-tr-lg" />
                            <div className="absolute bottom-0 left-0 w-6 h-6 border-b-2 border-l-2 border-white rounded-bl-lg" />
                            <div className="absolute bottom-0 right-0 w-6 h-6 border-b-2 border-r-2 border-white rounded-br-lg" />
                            <div className="absolute top-1/2 left-0 right-0 h-0.5 bg-blue-400/70 -translate-y-1/2 animate-pulse" />
                        </div>
                    </div>

                    {/* Status overlays */}
                    {status === 'loading' && (
                        <div className="absolute inset-0 flex flex-col items-center justify-center bg-black/60">
                            <Loader2 className="w-8 h-8 text-white animate-spin" />
                            <p className="text-white text-sm mt-2">Buscando produto...</p>
                        </div>
                    )}
                    {status === 'not_found' && (
                        <div className="absolute inset-0 flex flex-col items-center justify-center bg-black/60">
                            <AlertCircle className="w-8 h-8 text-amber-400" />
                            <p className="text-white text-sm mt-2">Produto não encontrado</p>
                            <p className="text-white/60 text-xs mt-1">Tente outro código</p>
                        </div>
                    )}
                    {status === 'error' && (
                        <div className="absolute inset-0 flex flex-col items-center justify-center bg-black/60 px-6 text-center">
                            <AlertCircle className="w-8 h-8 text-red-400" />
                            <p className="text-white text-sm mt-2">{errorMsg}</p>
                        </div>
                    )}
                </div>

                {/* Result */}
                {status === 'found' && found && (
                    <div className="p-4 space-y-3">
                        <div>
                            <p className="font-semibold text-gray-900 dark:text-gray-100 text-sm">{found.name}</p>
                            {found.brandName && (
                                <span className="text-xs px-2 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 rounded-full">
                                    {found.brandName}
                                </span>
                            )}
                        </div>
                        <div className="flex gap-3 text-xs font-semibold">
                            <span className={MACRO_COLORS.calories}>{found.caloriesKcal.toFixed(0)} kcal</span>
                            <span className={MACRO_COLORS.protein}>P {found.proteinG.toFixed(1)}g</span>
                            <span className={MACRO_COLORS.carbs}>C {found.carbsG.toFixed(1)}g</span>
                            <span className={MACRO_COLORS.fat}>G {found.fatG.toFixed(1)}g</span>
                            <span className="text-gray-400 font-normal">por 100g</span>
                        </div>
                        <div className="flex gap-2">
                            <button
                                onClick={handleAdd}
                                className="flex-1 py-2.5 bg-blue-600 text-white text-sm font-bold rounded-2xl hover:bg-blue-700 transition-colors active:scale-95"
                            >
                                Adicionar à refeição
                            </button>
                            <button
                                onClick={() => {
                                    lastBarcodeRef.current = '';
                                    setFound(null);
                                    setStatus('scanning');
                                }}
                                className="px-4 py-2.5 border border-black/15 dark:border-white/20 text-gray-600 dark:text-gray-400 text-sm rounded-2xl hover:bg-black/5 dark:hover:bg-white/5 transition-colors"
                            >
                                Novo scan
                            </button>
                        </div>
                    </div>
                )}

                {status === 'scanning' && (
                    <p className="text-center text-xs text-gray-400 py-3">
                        Aponte a câmera para o código de barras do produto
                    </p>
                )}
            </div>
        </div>
    );
}
