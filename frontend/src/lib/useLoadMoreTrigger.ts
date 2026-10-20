import { useEffect, useRef } from 'react'

/*
 *This hook sets up an IntersectionObserver on a div element to trigger a callback when the element is visible in the viewport. 
 *It is used for implementing infinite scrolling or "load more" functionality. 
 *The observer is only active when the `enabled` parameter is true, and it disconnects when the component unmounts or when `enabled` changes to false.
 *Parameters:
 *- onLoadMore: A callback function that is called when the observed element becomes visible.
 *- enabled: A boolean that determines whether the observer is active.
 *Returns:
 *- A ref object that should be attached to the div element to be observed.
*/

//simply infinite scroll instead of load more at the end
export function useLoadMoreTrigger(onLoadMore: () => void, enabled: boolean) {
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const node = ref.current
    if (!node || !enabled) return

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries.some((entry) => entry.isIntersecting)) onLoadMore()
      },
      { rootMargin: '600px' },
    )
    observer.observe(node)
    return () => observer.disconnect()
  }, [onLoadMore, enabled])

  return ref
}