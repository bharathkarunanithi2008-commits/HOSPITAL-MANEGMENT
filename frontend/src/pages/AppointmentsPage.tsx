import React, { useState } from 'react';
import { Appointment } from '../types';
import { Modal } from '../components/Modal';

export const AppointmentsPage: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([
    {
      appointment_id: 1,
      patient: 1,
      patient_name: 'Eleanor Vance',
      doctor: 1,
      doctor_name: 'Dr. Aris Thorne',
      doctor_specialization: 'Cardiology',
      appointment_date: '2026-09-25',
      appointment_time: '10:00',
      status: 'Scheduled',
      reason: 'Routine hypertension follow-up'
    },
    {
      appointment_id: 2,
      patient: 2,
      patient_name: 'David Miller',
      doctor: 3,
      doctor_name: 'Dr. Marcus Chen',
      doctor_specialization: 'Neurology',
      appointment_date: '2026-09-26',
      appointment_time: '14:30',
      status: 'Scheduled',
      reason: 'Recurrent migraine evaluation'
    }
  ]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [filterStatus, setFilterStatus] = useState('');
  const [form, setForm] = useState({
    patient_name: '',
    doctor_name: 'Dr. Aris Thorne (Cardiology)',
    appointment_date: '',
    appointment_time: '10:00',
    reason: ''
  });
  const [errorMsg, setErrorMsg] = useState('');

  const handleCreateAppointment = (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.patient_name || !form.appointment_date || !form.reason) {
      setErrorMsg('All fields are required.');
      return;
    }

    // Double booking check
    const clash = appointments.find(
      a => a.doctor_name?.includes('Dr. Aris Thorne') &&
      a.appointment_date === form.appointment_date &&
      a.appointment_time === form.appointment_time &&
      a.status !== 'Cancelled'
    );

    if (clash) {
      setErrorMsg(`Dr. Aris Thorne already has an appointment scheduled on ${form.appointment_date} at ${form.appointment_time}. Double booking prevented by SOP policy.`);
      return;
    }

    const newId = appointments.length + 1;
    setAppointments([
      {
        appointment_id: newId,
        patient: 1,
        patient_name: form.patient_name,
        doctor: 1,
        doctor_name: 'Dr. Aris Thorne',
        doctor_specialization: 'Cardiology',
        appointment_date: form.appointment_date,
        appointment_time: form.appointment_time,
        status: 'Scheduled',
        reason: form.reason
      },
      ...appointments
    ]);
    setIsModalOpen(false);
  };

  const updateStatus = (id: number, newStatus: Appointment['status']) => {
    setAppointments(appointments.map(a => a.appointment_id === id ? { ...a, status: newStatus } : a));
  };

  const filteredAppointments = appointments.filter(a =>
    filterStatus ? a.status === filterStatus : true
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Appointment Scheduling & Consultation Roster</h1>
          <p className="text-sm text-slate-500">Coordinate patient bookings, consultation timeslots, and attendance status</p>
        </div>
        <button
          onClick={() => {
            setErrorMsg('');
            setIsModalOpen(true);
          }}
          className="bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold px-4 py-2.5 rounded-xl shadow-sm transition-colors"
        >
          + Book Appointment
        </button>
      </div>

      <div className="flex gap-3 bg-white p-4 rounded-xl border border-slate-200">
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm text-slate-700"
        >
          <option value="">All Consultation Statuses</option>
          <option value="Scheduled">Scheduled</option>
          <option value="In-Progress">In-Progress</option>
          <option value="Completed">Completed</option>
          <option value="Cancelled">Cancelled</option>
        </select>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-600">
            <thead className="bg-slate-50 border-b border-slate-200 text-xs uppercase font-semibold text-slate-500">
              <tr>
                <th className="px-6 py-3.5">Patient</th>
                <th className="px-6 py-3.5">Doctor & Specialty</th>
                <th className="px-6 py-3.5">Date & Time</th>
                <th className="px-6 py-3.5">Status</th>
                <th className="px-6 py-3.5">Reason</th>
                <th className="px-6 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredAppointments.map(a => (
                <tr key={a.appointment_id} className="hover:bg-slate-50/80">
                  <td className="px-6 py-4 font-bold text-slate-900">{a.patient_name}</td>
                  <td className="px-6 py-4">
                    <div className="font-medium text-slate-900">{a.doctor_name}</div>
                    <div className="text-xs text-slate-400">{a.doctor_specialization}</div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="font-semibold text-slate-900">{a.appointment_date}</div>
                    <div className="text-xs text-slate-500">{a.appointment_time}</div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold ${
                      a.status === 'Completed' ? 'bg-emerald-50 text-emerald-700' :
                      a.status === 'Scheduled' ? 'bg-blue-50 text-blue-700' :
                      a.status === 'In-Progress' ? 'bg-amber-50 text-amber-700' : 'bg-rose-50 text-rose-700'
                    }`}>
                      {a.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-xs text-slate-500 max-w-xs truncate">{a.reason}</td>
                  <td className="px-6 py-4 text-right space-x-2">
                    {a.status !== 'Completed' && a.status !== 'Cancelled' && (
                      <>
                        <button
                          onClick={() => updateStatus(a.appointment_id, 'Completed')}
                          className="text-xs font-semibold text-emerald-600 hover:text-emerald-800"
                        >
                          Complete
                        </button>
                        <button
                          onClick={() => updateStatus(a.appointment_id, 'Cancelled')}
                          className="text-xs font-semibold text-rose-600 hover:text-rose-800"
                        >
                          Cancel
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Schedule Medical Consultation">
        <form onSubmit={handleCreateAppointment} className="space-y-4">
          {errorMsg && (
            <div className="p-3 rounded-lg bg-rose-50 text-rose-700 text-xs font-medium">
              {errorMsg}
            </div>
          )}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Patient Name *</label>
            <input
              type="text"
              required
              placeholder="e.g. Eleanor Vance"
              value={form.patient_name}
              onChange={(e) => setForm({ ...form, patient_name: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Assigned Physician *</label>
            <select
              value={form.doctor_name}
              onChange={(e) => setForm({ ...form, doctor_name: e.target.value })}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
            >
              <option value="Dr. Aris Thorne (Cardiology)">Dr. Aris Thorne (Cardiology)</option>
              <option value="Dr. Helen Mirren (Pediatrics)">Dr. Helen Mirren (Pediatrics)</option>
              <option value="Dr. Marcus Chen (Neurology)">Dr. Marcus Chen (Neurology)</option>
            </select>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Date *</label>
              <input
                type="date"
                required
                value={form.appointment_date}
                onChange={(e) => setForm({ ...form, appointment_date: e.target.value })}
                className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Time Slot *</label>
              <select
                value={form.appointment_time}
                onChange={(e) => setForm({ ...form, appointment_time: e.target.value })}
                className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm"
              >
                <option value="09:00">09:00 AM</option>
                <option value="10:00">10:00 AM</option>
                <option value="11:30">11:30 AM</option>
                <option value="14:00">02:00 PM</option>
                <option value="15:30">03:30 PM</option>
              </select>
            </div>
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Chief Complaint / Reason *</label>
            <textarea
              required
              rows={3}
              placeholder="Clinical symptoms or consultation purpose..."
              value={form.reason}
              onChange={(e) => setForm({ ...form, reason: e.target.value })}
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
              Confirm Appointment
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
