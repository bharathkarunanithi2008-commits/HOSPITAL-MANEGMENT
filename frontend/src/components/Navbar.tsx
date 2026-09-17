import React from 'react';

interface NavbarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  currentRole: string;
  setCurrentRole: (role: string) => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  setActiveTab,
  currentRole,
  setCurrentRole,
}) => {
  const tabs = [
    { id: 'dashboard', label: 'Dashboard' },
    { id: 'patients', label: 'Patients' },
    { id: 'doctors', label: 'Doctors' },
    { id: 'appointments', label: 'Appointments' },
    { id: 'records', label: 'Medical Records' },
  ];

  const roles = ['Admin', 'Doctor', 'Receptionist', 'Patient'];

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-30 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center text-white font-bold text-xl shadow-md shadow-blue-500/20">
              +
            </div>
            <div>
              <span className="font-bold text-lg text-slate-900 tracking-tight">Hospital Management</span>
              <span className="ml-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700">Enterprise HMS</span>
            </div>
          </div>

          <nav className="hidden md:flex space-x-1">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`px-3.5 py-2 text-sm font-medium rounded-lg transition-all ${
                  activeTab === tab.id
                    ? 'bg-blue-50 text-blue-700 font-semibold shadow-xs'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </nav>

          <div className="flex items-center space-x-2">
            <label className="text-xs text-slate-500 font-medium">Role:</label>
            <select
              value={currentRole}
              onChange={(e) => setCurrentRole(e.target.value)}
              className="text-xs font-semibold bg-slate-100 border border-slate-300 rounded-md px-2.5 py-1.5 text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {roles.map((r) => (
                <option key={r} value={r}>
                  {r}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Mobile nav bar */}
      <div className="md:hidden flex overflow-x-auto border-t border-slate-100 px-2 py-1 space-x-1">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-3 py-1.5 text-xs whitespace-nowrap rounded-md ${
              activeTab === tab.id
                ? 'bg-blue-600 text-white font-medium'
                : 'text-slate-600 hover:bg-slate-100'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>
    </header>
  );
};
