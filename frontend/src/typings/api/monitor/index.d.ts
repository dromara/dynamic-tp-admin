declare namespace Api {
  /**
   * namespace Monitor
   *
   * backend api module:"monitor"
   */
  namespace Monitor {
    // Re-export all monitor types
    export * from './logs-error';
    export * from './logs-operation';
    export * from './logs-login';
    export * from './file';
    export * from './client';
    export * from './properties';
  }
}
