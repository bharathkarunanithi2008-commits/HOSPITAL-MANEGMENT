import React, { useState } from 'react';
import { Doctor } from '../types';
import { Modal } from '../components/Modal';

export const DoctorsPage: React.FC = () => {
  const [doctors, setDoctors] = useState<Doctor[]>([
    {
      doctor_id: 1,
      full_name: 'Dr. Aris Thorne',
      specialization: 'Cardiology',
      department_name: 'Cardiology',
      phone: '+1-555-0101',
      email: 'dr.thorne@hospital.org',
      availability: 'Mon-Fri 08:30 - 16:30'
    },
    {
      doctor_id: 2,
      full_name: 'Dr. Helen Mirren',
      specialization: 'Pediatrics',
      department_name: 'Pediatrics',
      phone: '+1-555-0102',
      email: 'dr.mirren@hospital.org',
      availability: 'Mon-Thu 09:00 - 15:00'
    },
    {
      doctor_id: 3,
      full_name: 'Dr. Marcus Chen',
      specialization: 'Neurology',
      department_name: 'Neurology',
      phone: '+1-555-0103',
      email: 'dr.chen@hospital.org',
      availability: 'Tue-Sat 10:00 - 18:00'
    }
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [form, setForm] = useState({
    full_name: '',
    specialization: '',
    phone: '',
    email: '',
    availability: 'Mon-Fri 09:00 - 17:00'
  });

  const handleAddDoctor = (e: React.FormEvent) => {
    e.preventDefault();
    const newId = doctors.length + 1;
    setDoctors([...doctors, { doctor_id: newId, ...form, department_name: form.specialization }]);
    setIsModalOpen(false);
  };

  const filteredDoctors = doctors.filter(d =>
    d.full_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    d.specialization.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Medical Staff & Specialists</h1>
          <p className="text-sm text-slate-500">View attending physicians, clinical departments, and consultation shifts</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold px-4 py-2.5 rounded-xl shadow-sm transition-colors"
        >
          + Add Specialist
        </button>
      </div>

      <div className="bg-white p-4 rounded-xl border border-slate-200">
        <input
          type="text"
          placeholder="Filter doctors by name or specialization..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {filteredDoctors.map(doc => (
          <div key={doc.doctor_id} className="bg-white rounded-xl border border-slate-200 p-5 shadow-xs hover:shadow-md transition-shadow">
            <div className="flex items-start justify-between">
              <div>
                <h3 className="font-bold text-slate-900 text-base">{doc.full_name}</h3>
                <span className="inline-block mt-1 text-xs font-semibold px-2.5 py-0.5 rounded-md bg-blue-50 text-blue-700">
                  {doc.specialization}
                </span>
              </div>
              <span className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-xs font-bold text-slate-600">
                MD
              </span>
            </div>

            <div className="mt-4 space-y-1.5 text-xs text-slate-600">
              <div><span className="text-slate-400">Email:</span> {doc.email}</div>
              <div><span className="text-slate-400">Phone:</span> {doc.phone}</div>
              <div><span className="text-slate-400">Hours:</span> {doc.availability}</div>
            </div>
          </div>
        ))}
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register Hospital Specialist">
        <form onSubmit={handleAddDoctor} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Doctor Name *</label>
            <input
              type="text"
              required
              placeholder="Dr. Full Name"
              value={form.full_name}
              onChange={(e) => setForm({ ...form, full_name: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Specialization *</label>
            <input
              type="text"
              required
              placeholder="e.g. Cardiology, Pediatrics"
              value={form.specialization}
              onChange={(e) => setForm({ ...form, specialization: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Email Address *</label>
            <input
              type="email"
              required
              placeholder="doctor@hospital.org"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Phone Number *</label>
            <input
              type="text"
              required
              placeholder="+1-555-0100"
              value={form.phone}
              onChange={(e) => setForm({ ...form, phone: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Working Hours *</label>
            <input
              type="text"
              required
              value={form.availability}
              onChange={(e) => setForm({ ...form, availability: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div className="flex justify-end gap-2 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsModalOpen(false)}
              className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-lg shadow-sm"
            >
              Add Specialist
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
