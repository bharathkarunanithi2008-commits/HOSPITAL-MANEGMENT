import React from 'react';

interface StatCardProps {
  title: string;
  value: number | string;
  subtitle?: string;
  color?: string;
}

export const StatCard: React.FC<StatCardProps> = ({ title, value, subtitle, color = 'blue' }) => {
  return (
    <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-xs hover:shadow-md transition-shadow">
      <div className="text-xs font-semibold uppercase tracking-wider text-slate-500 mb-1">{title}</div>
      <div className="text-3xl font-extrabold text-slate-900">{value}</div>
      {subtitle && <div className="text-xs text-slate-400 mt-2">{subtitle}</div>}
    </div>
  );
};
