import React, { useState, useEffect } from 'react';
import { Patient } from '../types';
import { api } from '../services/api';
import { Modal } from '../components/Modal';

export const PatientsPage: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([
    {
      patient_id: 1,
      full_name: 'Eleanor Vance',
      date_of_birth: '1989-04-12',
      gender: 'Female',
      phone: '+1-555-0201',
      email: 'eleanor.vance@example.com',
      address: '42 Crestview Terrace, Boston, MA',
      blood_group: 'O+',
      emergency_contact: 'Marcus Vance (+1-555-0202)',
      appointments_count: 2,
      records_count: 1
    },
    {
      patient_id: 2,
      full_name: 'David Miller',
      date_of_birth: '1975-11-23',
      gender: 'Male',
      phone: '+1-555-0203',
      email: 'david.miller@example.com',
      address: '108 Beacon Hill Lane, Boston, MA',
      blood_group: 'A-',
      emergency_contact: 'Sarah Miller (+1-555-0204)',
      appointments_count: 1,
      records_count: 1
    }
  ]);
  const [searchTerm, setSearchTerm] = useState('');
  const [bloodFilter, setBloodFilter] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPatient, setEditingPatient] = useState<Patient | null>(null);

  const [form, setForm] = useState({
    full_name: '',
    date_of_birth: '',
    gender: 'Male' as const,
    phone: '',
    email: '',
    address: '',
    blood_group: 'O+' as const,
    emergency_contact: '',
  });
  const [errorMsg, setErrorMsg] = useState('');

  const handleOpenAdd = () => {
    setEditingPatient(null);
    setForm({
      full_name: '',
      date_of_birth: '',
      gender: 'Male',
      phone: '',
      email: '',
      address: '',
      blood_group: 'O+',
      emergency_contact: '',
    });
    setErrorMsg('');
    setIsModalOpen(true);
  };

  const handleOpenEdit = (p: Patient) => {
    setEditingPatient(p);
    setForm({
      full_name: p.full_name,
      date_of_birth: p.date_of_birth,
      gender: p.gender,
      phone: p.phone,
      email: p.email,
      address: p.address,
      blood_group: p.blood_group,
      emergency_contact: p.emergency_contact,
    });
    setErrorMsg('');
    setIsModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this patient record?')) return;
    try {
      await api.deletePatient(id);
    } catch {
      // Local fallback state deletion
    }
    setPatients(patients.filter((p) => p.patient_id !== id));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.full_name || !form.email || !form.phone || !form.date_of_birth) {
      setErrorMsg('All mandatory fields must be completed.');
      return;
    }

    if (editingPatient) {
      // Update
      try {
        await api.updatePatient(editingPatient.patient_id, form);
      } catch {
        // Fallback
      }
      setPatients(patients.map(p => p.patient_id === editingPatient.patient_id ? { ...p, ...form } : p));
    } else {
      // Create
      const newId = patients.length > 0 ? Math.max(...patients.map(p => p.patient_id)) + 1 : 1;
      const newPatient: Patient = {
        patient_id: newId,
        ...form,
        appointments_count: 0,
        records_count: 0
      };
      try {
        await api.createPatient(form);
      } catch {
        // Fallback
      }
      setPatients([newPatient, ...patients]);
    }
    setIsModalOpen(false);
  };

  const filteredPatients = patients.filter((p) => {
    const matchesSearch =
      p.full_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.phone.includes(searchTerm);
    const matchesBlood = bloodFilter ? p.blood_group === bloodFilter : true;
    return matchesSearch && matchesBlood;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Patient Directory & Registration</h1>
          <p className="text-sm text-slate-500">Manage patient demographics, contact details, and emergency baselines</p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold px-4 py-2.5 rounded-xl shadow-sm flex items-center gap-2 transition-colors"
        >
          <span>+ Register New Patient</span>
        </button>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-3 bg-white p-4 rounded-xl border border-slate-200">
        <input
          type="text"
          placeholder="Search by name, email, or phone..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="flex-1 bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <select
          value={bloodFilter}
          onChange={(e) => setBloodFilter(e.target.value)}
          className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">All Blood Groups</option>
          {['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'].map((bg) => (
            <option key={bg} value={bg}>{bg}</option>
          ))}
        </select>
      </div>

      {/* Patients Table */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-600">
            <thead className="bg-slate-50 border-b border-slate-200 text-xs uppercase font-semibold text-slate-500">
              <tr>
                <th className="px-6 py-3.5">Patient Details</th>
                <th className="px-6 py-3.5">Contact Info</th>
                <th className="px-6 py-3.5">Blood / Gender</th>
                <th className="px-6 py-3.5">Emergency Contact</th>
                <th className="px-6 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredPatients.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-8 text-center text-slate-400">
                    No patients match your search criteria.
                  </td>
                </tr>
              ) : (
                filteredPatients.map((p) => (
                  <tr key={p.patient_id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-bold text-slate-900">{p.full_name}</div>
                      <div className="text-xs text-slate-400">DOB: {p.date_of_birth} (ID: #{p.patient_id})</div>
                    </td>
                    <td className="px-6 py-4">
                      <div>{p.phone}</div>
                      <div className="text-xs text-slate-400">{p.email}</div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-rose-50 text-rose-700">
                        {p.blood_group}
                      </span>
                      <span className="ml-2 text-xs text-slate-500">{p.gender}</span>
                    </td>
                    <td className="px-6 py-4 text-xs">
                      <div className="font-medium text-slate-800">{p.emergency_contact}</div>
                      <div className="text-slate-400 truncate max-w-xs">{p.address}</div>
                    </td>
                    <td className="px-6 py-4 text-right space-x-2">
                      <button
                        onClick={() => handleOpenEdit(p)}
                        className="text-xs font-semibold text-blue-600 hover:text-blue-800"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(p.patient_id)}
                        className="text-xs font-semibold text-rose-600 hover:text-rose-800"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal Form */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingPatient ? 'Edit Patient Details' : 'Register New Patient'}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          {errorMsg && (
            <div className="p-3 rounded-lg bg-rose-50 text-rose-700 text-xs font-medium">
              {errorMsg}
            </div>
          )}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Full Name *</label>
            <input
              type="text"
              required
              value={form.full_name}
              onChange={(e) => setForm({ ...form, full_name: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Date of Birth *</label>
              <input
                type="date"
                required
                value={form.date_of_birth}
                onChange={(e) => setForm({ ...form, date_of_birth: e.target.value })}
                className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Gender *</label>
              <select
                value={form.gender}
                onChange={(e) => setForm({ ...form, gender: e.target.value as any })}
                className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
              >
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Phone Number *</label>
              <input
                type="text"
                required
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
                className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Blood Group *</label>
              <select
                value={form.blood_group}
                onChange={(e) => setForm({ ...form, blood_group: e.target.value as any })}
                className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
              >
                {['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'].map(bg => (
                  <option key={bg} value={bg}>{bg}</option>
                ))}
              </select>
            </div>
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Email Address *</label>
            <input
              type="email"
              required
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Address *</label>
            <input
              type="text"
              required
              value={form.address}
              onChange={(e) => setForm({ ...form, address: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Emergency Contact *</label>
            <input
              type="text"
              required
              placeholder="Name & Contact Phone"
              value={form.emergency_contact}
              onChange={(e) => setForm({ ...form, emergency_contact: e.target.value })}
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
              {editingPatient ? 'Save Changes' : 'Create Patient'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
