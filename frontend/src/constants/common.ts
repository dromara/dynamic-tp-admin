import { transformRecordToOption } from '@/utils/common';
import { $t } from '@/locales';

const themeColorRecord: Record<NaiveUI.ThemeColor, string> = {
  default: 'Default',
  error: 'Error',
  primary: 'Primary',
  info: 'Info',
  success: 'Success',
  warning: 'Warning'
};

export const themeColorOptions = transformRecordToOption(themeColorRecord);

/** generate code logic type */
type LogicType = 'and' | 'or';
const logicTypeRecord: Record<LogicType, string> = {
  and: 'AND',
  or: 'OR'
};
export const logicTypeOptions = transformRecordToOption(logicTypeRecord);
