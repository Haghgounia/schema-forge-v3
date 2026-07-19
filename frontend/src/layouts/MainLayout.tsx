import { NavLink, Outlet } from 'react-router-dom';

const navigation = [
  ['داشبورد', '/dashboard'],
  ['پروژه‌ها', '/projects'],
  ['مشخصات', '/specifications'],
  ['دیکشنری', '/dictionary'],
  ['Parser', '/parser'],
  ['اعتبارسنجی', '/validation'],
  ['تولید DDL', '/generation'],
  ['خروجی‌ها', '/artifacts'],
  ['گزارش‌ها', '/reports'],
  ['تنظیمات', '/settings'],
] as const;

export function MainLayout() {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <h1>SchemaForge v3</h1>
        <nav>
          {navigation.map(([label, path]) => (
            <NavLink key={path} to={path}>{label}</NavLink>
          ))}
        </nav>
      </aside>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
