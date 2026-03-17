interface ContributionGraphProps {
    dates: string[];
    weeks?: number;
}

const DAY_LABELS = ['M', 'T', 'W', 'T', 'F', 'S', 'S'];

function toDateString(d: Date): string {
    return d.toISOString().split('T')[0];
}

function computeStreak(checkInSet: Set<string>): number {
    let streak = 0;
    const cursor = new Date();
    cursor.setHours(0, 0, 0, 0);
    while (true) {
        if (checkInSet.has(toDateString(cursor))) {
            streak++;
            cursor.setDate(cursor.getDate() - 1);
        } else {
            break;
        }
    }
    return streak;
}

export function ContributionGraph({ dates, weeks = 16 }: ContributionGraphProps) {
    const checkInSet = new Set(dates.map(d => d.split('T')[0]));

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // Monday of current week
    const dow = today.getDay();
    const daysFromMon = dow === 0 ? 6 : dow - 1;
    const currentMon = new Date(today);
    currentMon.setDate(today.getDate() - daysFromMon);

    // Start = (weeks-1) weeks before currentMon
    const startDate = new Date(currentMon);
    startDate.setDate(currentMon.getDate() - (weeks - 1) * 7);

    const grid: Array<Array<{ dateStr: string; isFuture: boolean; hasCheckIn: boolean; isToday: boolean }>> = [];

    for (let w = 0; w < weeks; w++) {
        const week = [];
        for (let d = 0; d < 7; d++) {
            const cell = new Date(startDate);
            cell.setDate(startDate.getDate() + w * 7 + d);
            cell.setHours(0, 0, 0, 0);
            const dateStr = toDateString(cell);
            week.push({
                dateStr,
                isFuture: cell > today,
                isToday: cell.getTime() === today.getTime(),
                hasCheckIn: checkInSet.has(dateStr),
            });
        }
        grid.push(week);
    }

    const streak = computeStreak(checkInSet);
    const totalSessions = dates.length;

    return (
        <div>
            <div className="flex items-start gap-2">
                <div className="flex flex-col gap-0.5 mt-5 shrink-0">
                    {DAY_LABELS.map((l, i) => (
                        <span key={i} className="text-[9px] text-gray-400 dark:text-gray-600 h-3 leading-3">{i % 2 === 0 ? l : ''}</span>
                    ))}
                </div>
                <div className="flex gap-0.5 overflow-x-auto pb-1">
                    {grid.map((week, wi) => (
                        <div key={wi} className="flex flex-col gap-0.5">
                            {week.map((cell, di) => (
                                <div
                                    key={di}
                                    title={cell.dateStr}
                                    className={`w-3 h-3 rounded-sm transition-colors ${
                                        cell.isFuture
                                            ? 'opacity-0'
                                            : cell.hasCheckIn
                                            ? 'bg-green-400 dark:bg-green-500'
                                            : cell.isToday
                                            ? 'bg-gray-200 dark:bg-gray-700 ring-1 ring-blue-400'
                                            : 'bg-gray-100 dark:bg-gray-800'
                                    }`}
                                />
                            ))}
                        </div>
                    ))}
                </div>
            </div>
            <div className="flex items-center gap-4 mt-3">
                <div className="flex items-center gap-1.5">
                    <span className="text-lg font-bold text-green-500">{streak}</span>
                    <span className="text-xs text-gray-500 dark:text-gray-400">day streak</span>
                </div>
                <div className="w-px h-4 bg-gray-200 dark:bg-gray-700" />
                <div className="flex items-center gap-1.5">
                    <span className="text-lg font-bold text-gray-700 dark:text-gray-200">{totalSessions}</span>
                    <span className="text-xs text-gray-500 dark:text-gray-400">total sessions</span>
                </div>
                <div className="ml-auto flex items-center gap-1 text-[10px] text-gray-400">
                    <span>Less</span>
                    <div className="w-2.5 h-2.5 rounded-sm bg-gray-100 dark:bg-gray-800" />
                    <div className="w-2.5 h-2.5 rounded-sm bg-green-200 dark:bg-green-800" />
                    <div className="w-2.5 h-2.5 rounded-sm bg-green-400 dark:bg-green-500" />
                    <span>More</span>
                </div>
            </div>
        </div>
    );
}
