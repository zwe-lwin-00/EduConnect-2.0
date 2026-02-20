/**
 * Shared auth constants for role-based redirect and routes.
 * Use these everywhere instead of magic strings.
 */
export const AuthRoutes = {
  LOGIN: '/auth/login',
  ADMIN_HOME: '/admin',
  TEACHER_HOME: '/teacher',
  PARENT_HOME: '/parent',
} as const;

export const Roles = {
  ADMIN: 'ADMIN',
  TEACHER: 'TEACHER',
  PARENT: 'PARENT',
} as const;

export type Role = typeof Roles[keyof typeof Roles];

export const ROLE_HOME: Record<Role, string> = {
  [Roles.ADMIN]: AuthRoutes.ADMIN_HOME,
  [Roles.TEACHER]: AuthRoutes.TEACHER_HOME,
  [Roles.PARENT]: AuthRoutes.PARENT_HOME,
};
