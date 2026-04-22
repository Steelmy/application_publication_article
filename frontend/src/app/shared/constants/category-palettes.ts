export interface CategoriePalette {
  gradient: string;
  accent: string;
  chipBg: string;
  chipText: string;
}

const PALETTES: Record<string, CategoriePalette> = {
  Sciences: { gradient: 'from-emerald-300 via-teal-400 to-cyan-500', accent: 'text-emerald-700', chipBg: 'bg-emerald-100', chipText: 'text-emerald-800' },
  Littérature: { gradient: 'from-violet-300 via-purple-400 to-fuchsia-500', accent: 'text-violet-700', chipBg: 'bg-violet-100', chipText: 'text-violet-800' },
  Philosophie: { gradient: 'from-sky-300 via-indigo-400 to-blue-500', accent: 'text-indigo-700', chipBg: 'bg-indigo-100', chipText: 'text-indigo-800' },
  Histoire: { gradient: 'from-orange-300 via-amber-400 to-yellow-500', accent: 'text-amber-700', chipBg: 'bg-amber-100', chipText: 'text-amber-800' },
  Société: { gradient: 'from-rose-300 via-pink-400 to-fuchsia-500', accent: 'text-rose-700', chipBg: 'bg-rose-100', chipText: 'text-rose-800' },
  Technologie: { gradient: 'from-zinc-700 via-zinc-800 to-zinc-900', accent: 'text-zinc-700', chipBg: 'bg-zinc-100', chipText: 'text-zinc-800' },
  Arts: { gradient: 'from-amber-300 via-rose-400 to-fuchsia-500', accent: 'text-rose-700', chipBg: 'bg-rose-100', chipText: 'text-rose-800' },
};

const DEFAULT_PALETTE: CategoriePalette = {
  gradient: 'from-zinc-300 via-zinc-400 to-zinc-500',
  accent: 'text-zinc-700',
  chipBg: 'bg-zinc-100',
  chipText: 'text-zinc-800',
};

export function paletteFor(nomCategorie: string | undefined | null): CategoriePalette {
  return (nomCategorie && PALETTES[nomCategorie]) || DEFAULT_PALETTE;
}
