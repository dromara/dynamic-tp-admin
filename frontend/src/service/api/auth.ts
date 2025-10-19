import { request } from '../request';

/**
 * Login
 *
 * @param userName User name
 * @param password Password
 */
export function fetchLogin(userName: string, password: string) {
  return request<Api.Auth.LoginToken>({
    url: '/auth/user_name',
    method: 'post',
    data: {
      userName,
      password
    }
  });
}

/** user logout */
export function fetchLogout() {
  return request<boolean>({
    url: '/auth/logout',
    method: 'post'
  });
}

/** Get user info */
export function fetchGetUserInfo() {
  return request<Api.Auth.UserInfo>({ url: '/auth/user_info' });
}

/**
 * Refresh token
 *
 * @param refreshToken Refresh token
 */
export function fetchRefreshToken(refreshToken: string) {
  return request<Api.Auth.LoginToken>({
    url: '/auth/refresh_token',
    method: 'post',
    data: {
      refreshToken
    }
  });
}

/**
 * return custom backend error
 *
 * @param code error code
 * @param msg error message
 */
export function fetchCustomBackendError(code: string, msg: string) {
  return request({ url: '/auth/error', params: { code, msg } });
}

/**
 * Update current user info
 *
 * @param userInfo User info to update
 */
export function fetchUpdateCurrentUserInfo(userInfo: Api.Auth.UpdateCurrentUserInfo) {
  return request<Api.Auth.UserInfo>({
    url: '/auth/user_info',
    method: 'put',
    data: userInfo
  });
}

/**
 * Get login history
 *
 * @param params Search parameters
 */
export function fetchGetLoginHistory(params?: Api.Monitor.LoginHistorySearchParams) {
  return request<Api.Monitor.LoginHistoryList>({
    url: '/mon_logs_login/page',
    method: 'get',
    params
  });
}

/**
 * Change user password
 *
 * @param oldPassword Old password
 * @param newPassword New password
 */
export function fetchChangePassword(oldPassword: string, newPassword: string) {
  return request<boolean>({
    url: '/auth/change_password',
    method: 'put',
    data: {
      oldPassword,
      newPassword
    }
  });
}
