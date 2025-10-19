/**
 * 处理线程池名称，去掉@和client信息
 * @param poolName 原始线程池名称
 * @returns 处理后的线程池名称
 */
export function formatThreadPoolName(poolName: string): string {
  if (!poolName) return poolName;

  // 去掉@后面的信息
  const atIndex = poolName.indexOf('@');
  if (atIndex !== -1) {
    poolName = poolName.substring(0, atIndex);
  }

  // 去掉包含client的信息
  if (poolName.includes('client')) {
    // 如果包含client，尝试提取更简洁的名称
    // 例如: "myThreadPool-client-123" -> "myThreadPool"
    const parts = poolName.split('-');
    const filteredParts = parts.filter(part => !part.includes('client'));
    if (filteredParts.length > 0) {
      poolName = filteredParts.join('-');
    }
  }

  return poolName.trim();
}
