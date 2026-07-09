import React from 'react';
import { useFormContext } from 'react-hook-form';

interface FormSelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  name: string;
  label: string;
  options: { value: string | number; label: string }[];
}

export const FormSelect: React.FC<FormSelectProps> = ({ name, label, options, ...props }) => {
  const { register, formState: { errors } } = useFormContext();
  const error = errors[name]?.message as string;

  return (
    <div className="form-group">
      <label className="form-label" htmlFor={name}>{label}</label>
      <select
        id={name}
        className={`form-select ${error ? 'is-invalid' : ''}`}
        {...register(name, {
          setValueAs: (v) => {
            if (v === "") return undefined;
            if (!isNaN(Number(v)) && props.typeof === 'number') return Number(v);
            return v;
          }
        })}
        {...props}
      >
        <option value="">選択してください</option>
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      {error && <span className="form-error">{error}</span>}
    </div>
  );
};
