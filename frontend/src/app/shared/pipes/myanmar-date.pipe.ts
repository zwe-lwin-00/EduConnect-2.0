import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

/** Myanmar (Asia/Yangon) time zone, UTC+6:30. Use for all user-facing dates/times. */
export const MYANMAR_TIMEZONE = '+0630';

/**
 * Formats a date/time for display in Myanmar (UTC+6:30).
 * Pass an optional format (default 'medium') and the pipe uses +0630 so API UTC (Z) values display correctly.
 */
@Pipe({ name: 'myanmarDate' })
export class MyanmarDatePipe implements PipeTransform {
  private readonly datePipe = new DatePipe('en-US');

  transform(value: string | number | Date | null | undefined, format: string = 'medium'): string | null {
    if (value == null) return null;
    return this.datePipe.transform(value, format, MYANMAR_TIMEZONE);
  }
}
