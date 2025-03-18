export function useCosmeticImages() {
  const images = import.meta.glob('@/cosmeticsPictures/*', {eager: true, as: 'url'})

  function getImagePath(imageName: string): string {
    return images[`/src/cosmeticsPictures/${imageName}`] || ''
  }

  return {
    getImagePath
  }
}
