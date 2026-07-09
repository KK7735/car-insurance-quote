import React from 'react';
import { useFormContext } from 'react-hook-form';

interface FormInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  name: string;
  label: string;
  type?: string;
}

export const FormInput: React.FC<FormInputProps> = ({ name, label, type = 'text', ...props }) => {
  const { register, formState: { errors } } = useFormContext();
  const error = errors[name]?.message as string;

  return (
    <div className="form-group">
      <label className="form-label" htmlFor={name}>{label}</label>
      <input
        id={name}
        type={type}
        className={`form-input ${error ? 'is-invalid' : ''}`}
        {...register(name, {
          setValueAs: (v) => {
            if (type === 'number') {
              return v === '' || v === null || v === undefined ? undefined : Number(v);
            }
            return v;
          }
        })}
        {...props}
      />
      {error && <span className="form-error">{error}</span>}
    </div>
  );
};
