import type { Page } from './types'

/** Is there another page after this one? Works with either JSON shape of Spring's Page. */
export function hasNextPage(page: Page<unknown>, pageIndex: number, pageSize: number): boolean {
  if (typeof page.last === 'boolean') return !page.last
  if (page.page) return pageIndex + 1 < page.page.totalPages
  return page.content.length >= pageSize // no metadata: a full page probably means more
}