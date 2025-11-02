import React from 'react';
import { Input } from 'antd';

const PhoneInput = ({ value, onChange, ...props }) => {
  const formatPhoneNumber = (value) => {
    if (!value) return value;
    
    // Удаляем все нецифровые символы
    const phoneNumber = value.replace(/\D/g, '');
    
    // Форматируем номер
    let formattedNumber = '';
    if (phoneNumber.length > 0) {
      formattedNumber = '+375';
      if (phoneNumber.length > 3) {
        formattedNumber += ` (${phoneNumber.slice(3, 5)}`;
        if (phoneNumber.length > 5) {
          formattedNumber += `) ${phoneNumber.slice(5, 8)}`;
          if (phoneNumber.length > 8) {
            formattedNumber += `-${phoneNumber.slice(8, 10)}`;
            if (phoneNumber.length > 10) {
              formattedNumber += `-${phoneNumber.slice(10, 12)}`;
            }
          }
        }
      }
    }
    
    return formattedNumber;
  };

  const handleChange = (e) => {
    const { value } = e.target;
    const formattedValue = formatPhoneNumber(value);
    onChange(formattedValue);
  };

  return (
    <Input
      {...props}
      value={value}
      onChange={handleChange}
      maxLength={19} // +375 (XX) XXX-XX-XX
    />
  );
};

export default PhoneInput; 