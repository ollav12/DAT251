export function formatDuration(seconds: number): string {
  // Handle edge cases
  if (seconds === 0) return '0 seconds'
  if (isNaN(seconds) || seconds < 0) return 'Invalid duration'

  // Less than a minute
  if (seconds < 60) {
    return `${Math.round(seconds)} ${seconds === 1 ? 'second' : 'seconds'}`
  }

  // Less than an hour
  if (seconds < 3600) {
    const minutes = seconds / 60
    if (minutes === Math.floor(minutes)) {
      // Whole number of minutes
      return `${minutes} ${minutes === 1 ? 'minute' : 'minutes'}`
    } else {
      // Decimal minutes (rounded to 1 decimal place)
      return `${minutes.toFixed(1)} minutes`
    }
  }

  // Hours
  const hours = seconds / 3600
  if (hours === Math.floor(hours)) {
    // Whole number of hours
    return `${hours} ${hours === 1 ? 'hour' : 'hours'}`
  } else {
    // Decimal hours (rounded to 2 decimal places)
    return `${hours.toFixed(2)} hours`
  }
}
