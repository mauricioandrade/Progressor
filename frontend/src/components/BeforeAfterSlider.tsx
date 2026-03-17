import { useCallback, useRef, useState } from 'react';
import { MoveHorizontal } from 'lucide-react';

interface BeforeAfterSliderProps {
    beforeSrc: string;
    afterSrc: string;
    beforeLabel?: string;
    afterLabel?: string;
}

export function BeforeAfterSlider({
    beforeSrc,
    afterSrc,
    beforeLabel = 'Before',
    afterLabel = 'After',
}: BeforeAfterSliderProps) {
    const [position, setPosition] = useState(50);
    const containerRef = useRef<HTMLDivElement>(null);
    const isDragging = useRef(false);

    const updatePosition = useCallback((clientX: number) => {
        const el = containerRef.current;
        if (!el) return;
        const rect = el.getBoundingClientRect();
        const pct = Math.max(2, Math.min(98, ((clientX - rect.left) / rect.width) * 100));
        setPosition(pct);
    }, []);

    const onMouseDown = (e: React.MouseEvent) => {
        isDragging.current = true;
        updatePosition(e.clientX);
        e.preventDefault();
    };
    const onMouseMove = (e: React.MouseEvent) => {
        if (!isDragging.current) return;
        updatePosition(e.clientX);
    };
    const onMouseUp = () => { isDragging.current = false; };

    const onTouchStart = (e: React.TouchEvent) => {
        isDragging.current = true;
        updatePosition(e.touches[0].clientX);
    };
    const onTouchMove = (e: React.TouchEvent) => {
        if (!isDragging.current) return;
        updatePosition(e.touches[0].clientX);
    };
    const onTouchEnd = () => { isDragging.current = false; };

    return (
        <div
            ref={containerRef}
            className="relative w-full overflow-hidden rounded-2xl select-none cursor-col-resize bg-black"
            style={{ aspectRatio: '4 / 3' }}
            onMouseDown={onMouseDown}
            onMouseMove={onMouseMove}
            onMouseUp={onMouseUp}
            onMouseLeave={onMouseUp}
            onTouchStart={onTouchStart}
            onTouchMove={onTouchMove}
            onTouchEnd={onTouchEnd}
        >
            {/* After — full width, behind */}
            <img
                src={afterSrc}
                alt="After"
                draggable={false}
                className="absolute inset-0 w-full h-full object-cover pointer-events-none"
            />

            {/* Before — clipped to left side */}
            <div
                className="absolute inset-0 overflow-hidden pointer-events-none"
                style={{ width: `${position}%` }}
            >
                <img
                    src={beforeSrc}
                    alt="Before"
                    draggable={false}
                    className="absolute inset-0 h-full object-cover pointer-events-none"
                    style={{ width: `${(100 / position) * 100}%` }}
                />
            </div>

            {/* Divider line */}
            <div
                className="absolute top-0 bottom-0 w-px bg-white/90 shadow-[0_0_8px_rgba(0,0,0,0.6)] pointer-events-none"
                style={{ left: `${position}%` }}
            >
                {/* Handle knob */}
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-9 h-9 rounded-full bg-white shadow-lg flex items-center justify-center">
                    <MoveHorizontal className="w-4 h-4 text-gray-700" />
                </div>
            </div>

            {/* Labels */}
            <span className="absolute top-2.5 left-3 text-[11px] font-bold text-white bg-black/50 backdrop-blur-sm rounded-lg px-2 py-0.5 pointer-events-none">
                {beforeLabel}
            </span>
            <span className="absolute top-2.5 right-3 text-[11px] font-bold text-white bg-black/50 backdrop-blur-sm rounded-lg px-2 py-0.5 pointer-events-none">
                {afterLabel}
            </span>
        </div>
    );
}
