/**
 * Parse a backend time string (Beijing time, no timezone) as a Date object.
 * Backend returns "yyyy-MM-dd HH:mm:ss" in Asia/Shanghai timezone.
 */
export function parseBeijingTime(timeStr: string): Date {
  // Append +08:00 so JavaScript correctly interprets it as Beijing time
  return new Date(timeStr.replace(' ', 'T') + '+08:00');
}

/**
 * Format a backend time string to a locale string in Beijing timezone.
 */
export function formatBeijingTime(timeStr: string): string {
  return parseBeijingTime(timeStr).toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' });
}

export function formatBeijingDate(timeStr: string): string {
  return parseBeijingTime(timeStr).toLocaleDateString('zh-CN', { timeZone: 'Asia/Shanghai' });
}
