export interface Measurement {
    id: string;
    weight: number | null;
    bodyFatPercentage: number | null;
    chest: number | null;
    waist: number | null;
    hips: number | null;
    recordedAt: string;
}

export interface ProgressPhoto {
    id: string;
    imageBase64: string;
    takenAt: string;
    description: string;
    studentNotes: string | null;
    professionalFeedback: string | null;
}
