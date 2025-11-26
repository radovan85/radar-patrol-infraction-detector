// sort-utils.ts
export type SortDir = 'asc' | 'desc';

/**
 * Normalizuje vrednost za poređenje.
 * - Brojevi se porede direktno
 * - Stringovi se porede kao lowercase
 * - Ako string liči na datum, poredi se kao timestamp
 */
export function normalize(v: any): number | string {
  if (v == null) return '';
  if (typeof v === 'number') return v;
  if (typeof v === 'string') {
    const asDate = Date.parse(v);
    return isNaN(asDate) ? v.toLowerCase() : asDate;
  }
  return String(v).toLowerCase();
}

/**
 * Sortira niz objekata po zadatom ključu i smeru.
 * @param arr niz objekata
 * @param key polje po kojem se sortira
 * @param dir smer ('asc' ili 'desc')
 */
export function sortByKey<T extends Record<string, any>>(
  arr: T[],
  key: keyof T,
  dir: SortDir = 'asc'
): T[] {
  return [...arr].sort((a, b) => {
    const va = normalize(a[key]);
    const vb = normalize(b[key]);

    if (va < vb) return dir === 'asc' ? -1 : 1;
    if (va > vb) return dir === 'asc' ? 1 : -1;
    return 0;
  });
}
