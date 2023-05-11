package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Sabahattin on 2.03.2017.
 */

public class DatabaseForPlaylists extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "playlistsDB";

    public DatabaseForPlaylists(Context ct){
        super(ct, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //SearchResultDB
        db.execSQL("Create Table tbl_search_history(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "searchWord TEXT"
                +",videoId TEXT"
                +",videoTitle TEXT"
                +",viewCount TEXT"
                +",duration TEXT"
                +",publishDate TEXT"
                +",channelTitle TEXT"
                +",videoImgUrl TEXT)");

        db.execSQL("Create Table tbl_favorite(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "videoId TEXT"
                +",videoTitle TEXT"
                +",channelTitle TEXT"
                +",videoImgUrl TEXT)");
        db.execSQL("Create Table tbl_history(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "videoId TEXT"
                +",videoTitle TEXT"
                +",channelTitle TEXT"
                +",videoImgUrl TEXT"
                +",viewCount INTEGER DEFAULT 1)");
        db.execSQL("Create Table tbl_recommented(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "vidIdForSimilarVids TEXT"
                +",videoId TEXT"
                +",videoTitle TEXT"
                +",channelTitle TEXT"
                +",videoImgUrl TEXT)");
        db.execSQL("Create Table tbl_fromyoutube_videos(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "videoId TEXT"
                +",videoTitle TEXT"
                +",channelTitle TEXT"
                +",videoImgUrl TEXT"
                +",playlistId INTEGER)");
        db.execSQL("Create Table tbl_fromyoutube_playlist(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "playlistName TEXT,videoCount INTEGER,isYoutubePlaylist BOOLEAN DEFAULT 1)");
        String query = "CREATE TRIGGER if not exists deleteEmptyPlaylist "
                + " AFTER UPDATE "
                + " ON tbl_fromyoutube_playlist "
                + " BEGIN "
                + " delete from tbl_fromyoutube_playlist where videoCount=0;"
                + " END;";
        db.execSQL(query);

        db.execSQL("Create Table tbl_countries(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "flagUrl TEXT"
                +",countryCode VARCHAR(2)"
                +",countryName TEXT)");

        db.execSQL("Create Table tbl_discover_playlists(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "searchWord TEXT"
                +",playlistId TEXT"
                +",playlistName TEXT"
                +",thumbnailUrl TEXT"
                +",videoCount INTEGER DEFAULT 0)");

        db.execSQL("INSERT INTO tbl_countries VALUES ( null, 'http://sscoderr.com/countryFlags/AF.PNG', 'AF', 'Afghanistan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AL.PNG', 'AL', 'Albania'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/DZ.PNG', 'DZ', 'Algeria'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AS.PNG', 'AS', 'American Samoa'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AD.PNG', 'AD', 'Andorra'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AO.PNG', 'AO', 'Angola'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AI.PNG', 'AI', 'Anguilla'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AQ.PNG', 'AQ', 'Antarctica'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AG.PNG', 'AG', 'Antigua and Barbuda'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AR.PNG', 'AR', 'Argentina'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AM.PNG', 'AM', 'Armenia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AW.PNG', 'AW', 'Aruba'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AU.PNG', 'AU', 'Australia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AT.PNG', 'AT', 'Austria'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AZ.PNG', 'AZ', 'Azerbaijan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BS.PNG', 'BS', 'Bahamas'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BH.PNG', 'BH', 'Bahrain'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BD.PNG', 'BD', 'Bangladesh'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BB.PNG', 'BB', 'Barbados'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BY.PNG', 'BY', 'Belarus'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BE.PNG', 'BE', 'Belgium'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BZ.PNG', 'BZ', 'Belize'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BJ.PNG', 'BJ', 'Benin'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BM.PNG', 'BM', 'Bermuda'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BT.PNG', 'BT', 'Bhutan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BO.PNG', 'BO', 'Bolivia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BA.PNG', 'BA', 'Bosnia and Herzegovina'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BW.PNG', 'BW', 'Botswana'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BV.PNG', 'BV', 'Bouvet Island'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BR.PNG', 'BR', 'Brazil'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IO.PNG', 'IO', 'British Indian Ocean Territory'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BN.PNG', 'BN', 'Brunei Darussalam'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BG.PNG', 'BG', 'Bulgaria'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BF.PNG', 'BF', 'Burkina Faso'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/BI.PNG', 'BI', 'Burundi'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KH.PNG', 'KH', 'Cambodia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CM.PNG', 'CM', 'Cameroon'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CA.PNG', 'CA', 'Canada'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CV.PNG', 'CV', 'Cape Verde'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KY.PNG', 'KY', 'Cayman Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CF.PNG', 'CF', 'Central African Republic'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TD.PNG', 'TD', 'Chad'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CL.PNG', 'CL', 'Chile'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CN.PNG', 'CN', 'China'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CX.PNG', 'CX', 'Christmas Island'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CC.PNG', 'CC', 'Cocos (Keeling) Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CO.PNG', 'CO', 'Colombia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KM.PNG', 'KM', 'Comoros'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CD.PNG', 'CD', 'Democratic Republic of the Congo'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CG.PNG', 'CG', 'Republic of Congo'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CK.PNG', 'CK', 'Cook Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CR.PNG', 'CR', 'Costa Rica'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/HR.PNG', 'HR', 'Croatia (Hrvatska)'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CU.PNG', 'CU', 'Cuba'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CY.PNG', 'CY', 'Cyprus'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CZ.PNG', 'CZ', 'Czech Republic'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/DK.PNG', 'DK', 'Denmark'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/DJ.PNG', 'DJ', 'Djibouti'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/DM.PNG', 'DM', 'Dominica'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/DO.PNG', 'DO', 'Dominican Republic'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/EC.PNG', 'EC', 'Ecuador'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/EG.PNG', 'EG', 'Egypt'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SV.PNG', 'SV', 'El Salvador'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GQ.PNG', 'GQ', 'Equatorial Guinea'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ER.PNG', 'ER', 'Eritrea'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/EE.PNG', 'EE', 'Estonia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ET.PNG', 'ET', 'Ethiopia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/FK.PNG', 'FK', 'Falkland Islands (Malvinas)'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/FO.PNG', 'FO', 'Faroe Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/FJ.PNG', 'FJ', 'Fiji'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/FI.PNG', 'FI', 'Finland'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/FR.PNG', 'FR', 'France'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GF.PNG', 'GF', 'French Guiana'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PF.PNG', 'PF', 'French Polynesia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TF.PNG', 'TF', 'French Southern Territories'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GA.PNG', 'GA', 'Gabon'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GM.PNG', 'GM', 'Gambia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GE.PNG', 'GE', 'Georgia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/DE.PNG', 'DE', 'Germany'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GH.PNG', 'GH', 'Ghana'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GI.PNG', 'GI', 'Gibraltar'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GG.PNG', 'GG', 'Guernsey'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GR.PNG', 'GR', 'Greece'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GL.PNG', 'GL', 'Greenland'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GD.PNG', 'GD', 'Grenada'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GP.PNG', 'GP', 'Guadeloupe'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GU.PNG', 'GU', 'Guam'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GT.PNG', 'GT', 'Guatemala'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GN.PNG', 'GN', 'Guinea'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GW.PNG', 'GW', 'Guinea-Bissau'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GY.PNG', 'GY', 'Guyana'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/HT.PNG', 'HT', 'Haiti'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/HM.PNG', 'HM', 'Heard and Mc Donald Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/HN.PNG', 'HN', 'Honduras'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/HK.PNG', 'HK', 'Hong Kong'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/HU.PNG', 'HU', 'Hungary'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IS.PNG', 'IS', 'Iceland'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IN.PNG', 'IN', 'India'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IM.PNG', 'IM', 'Isle of Man'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ID.PNG', 'ID', 'Indonesia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IR.PNG', 'IR', 'Iran (Islamic Republic of)'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IQ.PNG', 'IQ', 'Iraq'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IE.PNG', 'IE', 'Ireland'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IL.PNG', 'IL', 'Israel'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/IT.PNG', 'IT', 'Italy'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CI.PNG', 'CI', 'Ivory Coast'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/JE.PNG', 'JE', 'Jersey'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/JM.PNG', 'JM', 'Jamaica'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/JP.PNG', 'JP', 'Japan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/JO.PNG', 'JO', 'Jordan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KZ.PNG', 'KZ', 'Kazakhstan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KE.PNG', 'KE', 'Kenya'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KI.PNG', 'KI', 'Kiribati'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KP.PNG', 'KP', 'Korea, Democratic People''s Republic of'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KR.PNG', 'KR', 'Korea, Republic of'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/XK.PNG', 'XK', 'Kosovo'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KW.PNG', 'KW', 'Kuwait'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KG.PNG', 'KG', 'Kyrgyzstan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LA.PNG', 'LA', 'Lao People''s Democratic Republic'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LV.PNG', 'LV', 'Latvia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LB.PNG', 'LB', 'Lebanon'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LS.PNG', 'LS', 'Lesotho'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LR.PNG', 'LR', 'Liberia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LY.PNG', 'LY', 'Libyan Arab Jamahiriya'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LI.PNG', 'LI', 'Liechtenstein'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LT.PNG', 'LT', 'Lithuania'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LU.PNG', 'LU', 'Luxembourg'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MO.PNG', 'MO', 'Macau'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MK.PNG', 'MK', 'North Macedonia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MG.PNG', 'MG', 'Madagascar'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MW.PNG', 'MW', 'Malawi'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MY.PNG', 'MY', 'Malaysia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MV.PNG', 'MV', 'Maldives'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ML.PNG', 'ML', 'Mali'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MT.PNG', 'MT', 'Malta'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MH.PNG', 'MH', 'Marshall Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MQ.PNG', 'MQ', 'Martinique'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MR.PNG', 'MR', 'Mauritania'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MU.PNG', 'MU', 'Mauritius'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MX.PNG', 'MX', 'Mexico'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/FM.PNG', 'FM', 'Micronesia, Federated States of'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MD.PNG', 'MD', 'Moldova, Republic of'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MC.PNG', 'MC', 'Monaco'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MN.PNG', 'MN', 'Mongolia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ME.PNG', 'ME', 'Montenegro'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MS.PNG', 'MS', 'Montserrat'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MA.PNG', 'MA', 'Morocco'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MZ.PNG', 'MZ', 'Mozambique'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MM.PNG', 'MM', 'Myanmar'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NA.PNG', 'NA', 'Namibia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NR.PNG', 'NR', 'Nauru'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NP.PNG', 'NP', 'Nepal'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NL.PNG', 'NL', 'Netherlands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AN.PNG', 'AN', 'Netherlands Antilles'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NC.PNG', 'NC', 'New Caledonia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NZ.PNG', 'NZ', 'New Zealand'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NI.PNG', 'NI', 'Nicaragua'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NE.PNG', 'NE', 'Niger'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NG.PNG', 'NG', 'Nigeria'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NU.PNG', 'NU', 'Niue'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NF.PNG', 'NF', 'Norfolk Island'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/MP.PNG', 'MP', 'Northern Mariana Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/NO.PNG', 'NO', 'Norway'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/OM.PNG', 'OM', 'Oman'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PK.PNG', 'PK', 'Pakistan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PW.PNG', 'PW', 'Palau'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PS.PNG', 'PS', 'Palestine'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PA.PNG', 'PA', 'Panama'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PG.PNG', 'PG', 'Papua New Guinea'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PY.PNG', 'PY', 'Paraguay'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PE.PNG', 'PE', 'Peru'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PH.PNG', 'PH', 'Philippines'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PN.PNG', 'PN', 'Pitcairn'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PL.PNG', 'PL', 'Poland'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PT.PNG', 'PT', 'Portugal'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PR.PNG', 'PR', 'Puerto Rico'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/QA.PNG', 'QA', 'Qatar'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/RE.PNG', 'RE', 'Reunion'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/RO.PNG', 'RO', 'Romania'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/RU.PNG', 'RU', 'Russian Federation'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/RW.PNG', 'RW', 'Rwanda'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/KN.PNG', 'KN', 'Saint Kitts and Nevis'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LC.PNG', 'LC', 'Saint Lucia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/VC.PNG', 'VC', 'Saint Vincent and the Grenadines'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/WS.PNG', 'WS', 'Samoa'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SM.PNG', 'SM', 'San Marino'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ST.PNG', 'ST', 'Sao Tome and Principe'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SA.PNG', 'SA', 'Saudi Arabia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SN.PNG', 'SN', 'Senegal'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/RS.PNG', 'RS', 'Serbia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SC.PNG', 'SC', 'Seychelles'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SL.PNG', 'SL', 'Sierra Leone'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SG.PNG', 'SG', 'Singapore'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SK.PNG', 'SK', 'Slovakia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SI.PNG', 'SI', 'Slovenia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SB.PNG', 'SB', 'Solomon Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SO.PNG', 'SO', 'Somalia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ZA.PNG', 'ZA', 'South Africa'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GS.PNG', 'GS', 'South Georgia South Sandwich Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SS.PNG', 'SS', 'South Sudan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ES.PNG', 'ES', 'Spain'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/LK.PNG', 'LK', 'Sri Lanka'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SH.PNG', 'SH', 'St. Helena'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/PM.PNG', 'PM', 'St. Pierre and Miquelon'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SD.PNG', 'SD', 'Sudan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SR.PNG', 'SR', 'Suriname'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SJ.PNG', 'SJ', 'Svalbard and Jan Mayen Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SZ.PNG', 'SZ', 'Swaziland'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SE.PNG', 'SE', 'Sweden'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/CH.PNG', 'CH', 'Switzerland'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/SY.PNG', 'SY', 'Syrian Arab Republic'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TW.PNG', 'TW', 'Taiwan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TJ.PNG', 'TJ', 'Tajikistan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TZ.PNG', 'TZ', 'Tanzania, United Republic of'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TH.PNG', 'TH', 'Thailand'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TG.PNG', 'TG', 'Togo'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TK.PNG', 'TK', 'Tokelau'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TO.PNG', 'TO', 'Tonga'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TT.PNG', 'TT', 'Trinidad and Tobago'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TN.PNG', 'TN', 'Tunisia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TR.PNG', 'TR', 'Turkey'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TM.PNG', 'TM', 'Turkmenistan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TC.PNG', 'TC', 'Turks and Caicos Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/TV.PNG', 'TV', 'Tuvalu'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/UG.PNG', 'UG', 'Uganda'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/UA.PNG', 'UA', 'Ukraine'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/AE.PNG', 'AE', 'United Arab Emirates'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/GB.PNG', 'GB', 'United Kingdom'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/US.PNG', 'US', 'United States'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/UM.PNG', 'UM', 'United States minor outlying islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/UY.PNG', 'UY', 'Uruguay'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/UZ.PNG', 'UZ', 'Uzbekistan'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/VU.PNG', 'VU', 'Vanuatu'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/VA.PNG', 'VA', 'Vatican City State'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/VE.PNG', 'VE', 'Venezuela'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/VN.PNG', 'VN', 'Vietnam'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/VG.PNG', 'VG', 'Virgin Islands (British)'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/VI.PNG', 'VI', 'Virgin Islands (U.S.)'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/WF.PNG', 'WF', 'Wallis and Futuna Islands'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/EH.PNG', 'EH', 'Western Sahara'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/YE.PNG', 'YE', 'Yemen'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ZM.PNG', 'ZM', 'Zambia'),\n" +
                " ( null, 'http://sscoderr.com/countryFlags/ZW.PNG', 'ZW', 'Zimbabwe');");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1: {
                Log.e("DatabaseUpdated", "Updated");
                //tblFavorite
                db.execSQL("ALTER TABLE tbl_favorite ADD COLUMN videoTitle TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_favorite ADD COLUMN channelTitle TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_favorite ADD COLUMN videoImgUrl TEXT DEFAULT ''");
                //tblRecommented
                db.execSQL("ALTER TABLE tbl_recommented ADD COLUMN vidIdForSimilarVids TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_recommented ADD COLUMN videoTitle TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_recommented ADD COLUMN channelTitle TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_recommented ADD COLUMN videoImgUrl TEXT DEFAULT ''");
                //tblMostPlayed
                db.execSQL("ALTER TABLE tbl_history ADD COLUMN videoTitle TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_history ADD COLUMN channelTitle TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_history ADD COLUMN videoImgUrl TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_history ADD COLUMN viewCount INTEGER DEFAULT 1");
                //tblFromYoutube
                db.execSQL("ALTER TABLE tbl_fromyoutube_videos ADD COLUMN videoTitle TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_fromyoutube_videos ADD COLUMN channelTitle TEXT DEFAULT ''");
                db.execSQL("ALTER TABLE tbl_fromyoutube_videos ADD COLUMN videoImgUrl TEXT DEFAULT ''");
                //tblFromYoutubePlaylist
                db.execSQL("ALTER TABLE tbl_fromyoutube_playlist ADD COLUMN isYoutubePlaylist BOOLEAN DEFAULT 1");

                db.execSQL("Create Table tbl_countries(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "flagUrl TEXT"
                        + ",countryCode VARCHAR(2)"
                        + ",countryName TEXT)");

                db.execSQL("Create Table tbl_discover_playlists(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "searchWord TEXT"
                        +",playlistId TEXT"
                        +",playlistName TEXT"
                        +",thumbnailUrl TEXT"
                        +",videoCount INTEGER DEFAULT 0)");

                //SearchResultDB
                db.execSQL("Create Table tbl_search_history(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "searchWord TEXT"
                        +",videoId TEXT"
                        +",videoTitle TEXT"
                        +",viewCount TEXT"
                        +",duration TEXT"
                        +",publishDate TEXT"
                        +",channelTitle TEXT"
                        +",videoImgUrl TEXT)");

                db.execSQL("INSERT INTO tbl_countries VALUES ( null, 'http://sscoderr.com/countryFlags/AF.PNG', 'AF', 'Afghanistan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AL.PNG', 'AL', 'Albania'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/DZ.PNG', 'DZ', 'Algeria'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AS.PNG', 'AS', 'American Samoa'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AD.PNG', 'AD', 'Andorra'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AO.PNG', 'AO', 'Angola'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AI.PNG', 'AI', 'Anguilla'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AQ.PNG', 'AQ', 'Antarctica'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AG.PNG', 'AG', 'Antigua and Barbuda'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AR.PNG', 'AR', 'Argentina'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AM.PNG', 'AM', 'Armenia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AW.PNG', 'AW', 'Aruba'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AU.PNG', 'AU', 'Australia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AT.PNG', 'AT', 'Austria'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AZ.PNG', 'AZ', 'Azerbaijan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BS.PNG', 'BS', 'Bahamas'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BH.PNG', 'BH', 'Bahrain'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BD.PNG', 'BD', 'Bangladesh'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BB.PNG', 'BB', 'Barbados'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BY.PNG', 'BY', 'Belarus'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BE.PNG', 'BE', 'Belgium'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BZ.PNG', 'BZ', 'Belize'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BJ.PNG', 'BJ', 'Benin'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BM.PNG', 'BM', 'Bermuda'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BT.PNG', 'BT', 'Bhutan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BO.PNG', 'BO', 'Bolivia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BA.PNG', 'BA', 'Bosnia and Herzegovina'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BW.PNG', 'BW', 'Botswana'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BV.PNG', 'BV', 'Bouvet Island'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BR.PNG', 'BR', 'Brazil'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IO.PNG', 'IO', 'British Indian Ocean Territory'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BN.PNG', 'BN', 'Brunei Darussalam'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BG.PNG', 'BG', 'Bulgaria'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BF.PNG', 'BF', 'Burkina Faso'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/BI.PNG', 'BI', 'Burundi'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KH.PNG', 'KH', 'Cambodia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CM.PNG', 'CM', 'Cameroon'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CA.PNG', 'CA', 'Canada'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CV.PNG', 'CV', 'Cape Verde'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KY.PNG', 'KY', 'Cayman Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CF.PNG', 'CF', 'Central African Republic'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TD.PNG', 'TD', 'Chad'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CL.PNG', 'CL', 'Chile'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CN.PNG', 'CN', 'China'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CX.PNG', 'CX', 'Christmas Island'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CC.PNG', 'CC', 'Cocos (Keeling) Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CO.PNG', 'CO', 'Colombia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KM.PNG', 'KM', 'Comoros'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CD.PNG', 'CD', 'Democratic Republic of the Congo'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CG.PNG', 'CG', 'Republic of Congo'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CK.PNG', 'CK', 'Cook Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CR.PNG', 'CR', 'Costa Rica'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/HR.PNG', 'HR', 'Croatia (Hrvatska)'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CU.PNG', 'CU', 'Cuba'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CY.PNG', 'CY', 'Cyprus'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CZ.PNG', 'CZ', 'Czech Republic'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/DK.PNG', 'DK', 'Denmark'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/DJ.PNG', 'DJ', 'Djibouti'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/DM.PNG', 'DM', 'Dominica'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/DO.PNG', 'DO', 'Dominican Republic'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/EC.PNG', 'EC', 'Ecuador'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/EG.PNG', 'EG', 'Egypt'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SV.PNG', 'SV', 'El Salvador'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GQ.PNG', 'GQ', 'Equatorial Guinea'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ER.PNG', 'ER', 'Eritrea'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/EE.PNG', 'EE', 'Estonia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ET.PNG', 'ET', 'Ethiopia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/FK.PNG', 'FK', 'Falkland Islands (Malvinas)'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/FO.PNG', 'FO', 'Faroe Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/FJ.PNG', 'FJ', 'Fiji'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/FI.PNG', 'FI', 'Finland'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/FR.PNG', 'FR', 'France'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GF.PNG', 'GF', 'French Guiana'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PF.PNG', 'PF', 'French Polynesia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TF.PNG', 'TF', 'French Southern Territories'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GA.PNG', 'GA', 'Gabon'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GM.PNG', 'GM', 'Gambia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GE.PNG', 'GE', 'Georgia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/DE.PNG', 'DE', 'Germany'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GH.PNG', 'GH', 'Ghana'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GI.PNG', 'GI', 'Gibraltar'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GG.PNG', 'GG', 'Guernsey'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GR.PNG', 'GR', 'Greece'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GL.PNG', 'GL', 'Greenland'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GD.PNG', 'GD', 'Grenada'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GP.PNG', 'GP', 'Guadeloupe'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GU.PNG', 'GU', 'Guam'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GT.PNG', 'GT', 'Guatemala'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GN.PNG', 'GN', 'Guinea'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GW.PNG', 'GW', 'Guinea-Bissau'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GY.PNG', 'GY', 'Guyana'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/HT.PNG', 'HT', 'Haiti'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/HM.PNG', 'HM', 'Heard and Mc Donald Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/HN.PNG', 'HN', 'Honduras'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/HK.PNG', 'HK', 'Hong Kong'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/HU.PNG', 'HU', 'Hungary'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IS.PNG', 'IS', 'Iceland'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IN.PNG', 'IN', 'India'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IM.PNG', 'IM', 'Isle of Man'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ID.PNG', 'ID', 'Indonesia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IR.PNG', 'IR', 'Iran (Islamic Republic of)'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IQ.PNG', 'IQ', 'Iraq'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IE.PNG', 'IE', 'Ireland'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IL.PNG', 'IL', 'Israel'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/IT.PNG', 'IT', 'Italy'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CI.PNG', 'CI', 'Ivory Coast'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/JE.PNG', 'JE', 'Jersey'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/JM.PNG', 'JM', 'Jamaica'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/JP.PNG', 'JP', 'Japan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/JO.PNG', 'JO', 'Jordan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KZ.PNG', 'KZ', 'Kazakhstan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KE.PNG', 'KE', 'Kenya'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KI.PNG', 'KI', 'Kiribati'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KP.PNG', 'KP', 'Korea, Democratic People''s Republic of'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KR.PNG', 'KR', 'Korea, Republic of'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/XK.PNG', 'XK', 'Kosovo'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KW.PNG', 'KW', 'Kuwait'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KG.PNG', 'KG', 'Kyrgyzstan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LA.PNG', 'LA', 'Lao People''s Democratic Republic'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LV.PNG', 'LV', 'Latvia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LB.PNG', 'LB', 'Lebanon'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LS.PNG', 'LS', 'Lesotho'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LR.PNG', 'LR', 'Liberia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LY.PNG', 'LY', 'Libyan Arab Jamahiriya'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LI.PNG', 'LI', 'Liechtenstein'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LT.PNG', 'LT', 'Lithuania'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LU.PNG', 'LU', 'Luxembourg'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MO.PNG', 'MO', 'Macau'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MK.PNG', 'MK', 'North Macedonia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MG.PNG', 'MG', 'Madagascar'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MW.PNG', 'MW', 'Malawi'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MY.PNG', 'MY', 'Malaysia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MV.PNG', 'MV', 'Maldives'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ML.PNG', 'ML', 'Mali'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MT.PNG', 'MT', 'Malta'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MH.PNG', 'MH', 'Marshall Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MQ.PNG', 'MQ', 'Martinique'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MR.PNG', 'MR', 'Mauritania'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MU.PNG', 'MU', 'Mauritius'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MX.PNG', 'MX', 'Mexico'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/FM.PNG', 'FM', 'Micronesia, Federated States of'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MD.PNG', 'MD', 'Moldova, Republic of'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MC.PNG', 'MC', 'Monaco'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MN.PNG', 'MN', 'Mongolia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ME.PNG', 'ME', 'Montenegro'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MS.PNG', 'MS', 'Montserrat'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MA.PNG', 'MA', 'Morocco'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MZ.PNG', 'MZ', 'Mozambique'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MM.PNG', 'MM', 'Myanmar'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NA.PNG', 'NA', 'Namibia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NR.PNG', 'NR', 'Nauru'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NP.PNG', 'NP', 'Nepal'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NL.PNG', 'NL', 'Netherlands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AN.PNG', 'AN', 'Netherlands Antilles'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NC.PNG', 'NC', 'New Caledonia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NZ.PNG', 'NZ', 'New Zealand'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NI.PNG', 'NI', 'Nicaragua'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NE.PNG', 'NE', 'Niger'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NG.PNG', 'NG', 'Nigeria'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NU.PNG', 'NU', 'Niue'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NF.PNG', 'NF', 'Norfolk Island'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/MP.PNG', 'MP', 'Northern Mariana Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/NO.PNG', 'NO', 'Norway'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/OM.PNG', 'OM', 'Oman'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PK.PNG', 'PK', 'Pakistan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PW.PNG', 'PW', 'Palau'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PS.PNG', 'PS', 'Palestine'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PA.PNG', 'PA', 'Panama'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PG.PNG', 'PG', 'Papua New Guinea'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PY.PNG', 'PY', 'Paraguay'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PE.PNG', 'PE', 'Peru'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PH.PNG', 'PH', 'Philippines'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PN.PNG', 'PN', 'Pitcairn'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PL.PNG', 'PL', 'Poland'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PT.PNG', 'PT', 'Portugal'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PR.PNG', 'PR', 'Puerto Rico'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/QA.PNG', 'QA', 'Qatar'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/RE.PNG', 'RE', 'Reunion'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/RO.PNG', 'RO', 'Romania'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/RU.PNG', 'RU', 'Russian Federation'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/RW.PNG', 'RW', 'Rwanda'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/KN.PNG', 'KN', 'Saint Kitts and Nevis'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LC.PNG', 'LC', 'Saint Lucia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/VC.PNG', 'VC', 'Saint Vincent and the Grenadines'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/WS.PNG', 'WS', 'Samoa'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SM.PNG', 'SM', 'San Marino'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ST.PNG', 'ST', 'Sao Tome and Principe'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SA.PNG', 'SA', 'Saudi Arabia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SN.PNG', 'SN', 'Senegal'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/RS.PNG', 'RS', 'Serbia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SC.PNG', 'SC', 'Seychelles'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SL.PNG', 'SL', 'Sierra Leone'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SG.PNG', 'SG', 'Singapore'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SK.PNG', 'SK', 'Slovakia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SI.PNG', 'SI', 'Slovenia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SB.PNG', 'SB', 'Solomon Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SO.PNG', 'SO', 'Somalia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ZA.PNG', 'ZA', 'South Africa'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GS.PNG', 'GS', 'South Georgia South Sandwich Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SS.PNG', 'SS', 'South Sudan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ES.PNG', 'ES', 'Spain'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/LK.PNG', 'LK', 'Sri Lanka'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SH.PNG', 'SH', 'St. Helena'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/PM.PNG', 'PM', 'St. Pierre and Miquelon'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SD.PNG', 'SD', 'Sudan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SR.PNG', 'SR', 'Suriname'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SJ.PNG', 'SJ', 'Svalbard and Jan Mayen Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SZ.PNG', 'SZ', 'Swaziland'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SE.PNG', 'SE', 'Sweden'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/CH.PNG', 'CH', 'Switzerland'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/SY.PNG', 'SY', 'Syrian Arab Republic'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TW.PNG', 'TW', 'Taiwan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TJ.PNG', 'TJ', 'Tajikistan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TZ.PNG', 'TZ', 'Tanzania, United Republic of'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TH.PNG', 'TH', 'Thailand'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TG.PNG', 'TG', 'Togo'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TK.PNG', 'TK', 'Tokelau'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TO.PNG', 'TO', 'Tonga'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TT.PNG', 'TT', 'Trinidad and Tobago'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TN.PNG', 'TN', 'Tunisia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TR.PNG', 'TR', 'Turkey'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TM.PNG', 'TM', 'Turkmenistan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TC.PNG', 'TC', 'Turks and Caicos Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/TV.PNG', 'TV', 'Tuvalu'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/UG.PNG', 'UG', 'Uganda'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/UA.PNG', 'UA', 'Ukraine'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/AE.PNG', 'AE', 'United Arab Emirates'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/GB.PNG', 'GB', 'United Kingdom'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/US.PNG', 'US', 'United States'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/UM.PNG', 'UM', 'United States minor outlying islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/UY.PNG', 'UY', 'Uruguay'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/UZ.PNG', 'UZ', 'Uzbekistan'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/VU.PNG', 'VU', 'Vanuatu'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/VA.PNG', 'VA', 'Vatican City State'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/VE.PNG', 'VE', 'Venezuela'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/VN.PNG', 'VN', 'Vietnam'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/VG.PNG', 'VG', 'Virgin Islands (British)'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/VI.PNG', 'VI', 'Virgin Islands (U.S.)'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/WF.PNG', 'WF', 'Wallis and Futuna Islands'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/EH.PNG', 'EH', 'Western Sahara'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/YE.PNG', 'YE', 'Yemen'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ZM.PNG', 'ZM', 'Zambia'),\n" +
                        " ( null, 'http://sscoderr.com/countryFlags/ZW.PNG', 'ZW', 'Zimbabwe');");
            }//dont use break;
        }
    }
    public void addVideoFavorites(String videoId,String videoName,String channelName,String videoThumbnialUrl,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_favorite where videoId=?",new String[]{videoId.toString()});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("videoId", videoId);
        values.put("videoTitle",videoName);
        values.put("channelTitle",channelName);
        values.put("videoImgUrl",videoThumbnialUrl);
        if(cur.getCount()==0)
            dbTwo.insert("tbl_favorite", null, values);
        db.close();
    }
    public void addVideoHistory(String videoId,String videoName,String channelName,String videoThumbnialUrl,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT viewCount FROM tbl_history where videoId=?",new String[]{videoId.toString()});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("videoId", videoId);
        values.put("videoTitle",videoName);
        values.put("channelTitle",channelName);
        values.put("videoImgUrl",videoThumbnialUrl);
        if(cur.getCount()!=0){
            cur.moveToFirst();
            int viewCount=cur.getInt(0);
            values.put("viewCount",viewCount+1);
            dbTwo.delete("tbl_history","videoId=?",new String[]{videoId});
        }
        else values.put("viewCount",1);
        dbTwo.insert("tbl_history", null, values);
        db.close();
    }
    public void addVideoRecommented(String vidIdForSimilar,String videoId,String videoName,String channelName,String videoThumbnialUrl,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_recommented where videoId=?",new String[]{videoId.toString()});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("vidIdForSimilarVids", vidIdForSimilar);
        values.put("videoId", videoId);
        values.put("videoTitle",videoName);
        values.put("channelTitle",channelName);
        values.put("videoImgUrl",videoThumbnialUrl);
        if(cur.getCount()==0)
            dbTwo.insert("tbl_recommented", null, values);
        db.close();
    }
    public void addUserVideo(String videoId,String videoName,String channelName,String videoThumbnialUrl,Context ct,String playlistId) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_videos where videoId=? and playlistId=?",new String[]{videoId,playlistId});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("videoId", videoId);
        values.put("playlistId",playlistId);
        values.put("videoTitle",videoName);
        values.put("channelTitle",channelName);
        values.put("videoImgUrl",videoThumbnialUrl);
        if(cur.getCount()==0)
            dbTwo.insert("tbl_fromyoutube_videos", null, values);
        db.close();
    }
    public void addDiscoverPlaylists(String searchWord,String playlistId,String playlistName,String thumbnailUrl,String videoCount,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_discover_playlists where playlistId=?",new String[]{playlistId});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("searchWord",searchWord);
        values.put("playlistId", playlistId);
        values.put("playlistName",playlistName);
        values.put("thumbnailUrl",thumbnailUrl);
        values.put("videoCount",videoCount);
        if(cur.getCount()==0) {
            dbTwo.insert("tbl_discover_playlists", null, values);
        }
        db.close();
    }
    public void addSearchHistory(String searchWord,String videoId,String videoTitle,String thumbnailUrl,String viewCount,String channelTitle,String publishDate,String duration,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_search_history where videoId=?",new String[]{videoId});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("searchWord",searchWord);
        values.put("videoId", videoId);
        values.put("videoTitle",videoTitle);
        values.put("videoImgUrl",thumbnailUrl);
        values.put("viewCount",viewCount);
        values.put("channelTitle",channelTitle);
        values.put("publishDate",publishDate);
        values.put("duration",duration);

        if(cur.getCount()==0) {
            dbTwo.insert("tbl_search_history", null, values);
        }
        db.close();
    }
    public void addUserPlaylist(String playlistName,int videoCount,Context ct,int isYoutube) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where playlistName=?",new String[]{playlistName.toString()});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("playlistName", playlistName);
        values.put("videoCount",videoCount);
        values.put("isYoutubePlaylist",isYoutube);
        if(cur.getCount()==0)
            dbTwo.insert("tbl_fromyoutube_playlist", null, values);
        else {
            SQLiteDatabase dbForUpdate = this.getWritableDatabase();
            Cursor cursorOne = dbForUpdate.rawQuery("SELECT id FROM tbl_fromyoutube_playlist where playlistName=?",new String[]{playlistName});
            int playlistIdTemb=0;
            if( cursorOne != null && cursorOne.moveToFirst() ) {
                playlistIdTemb = Integer.parseInt(cursorOne.getString(0));
                cur.close();
            }

            dbTwo.delete("tbl_fromyoutube_playlist","playlistName=?",new String[]{playlistName});
            dbTwo.insert("tbl_fromyoutube_playlist", null, values);



            String query="SELECT id FROM tbl_fromyoutube_playlist order by id desc LIMIT 1";
            Cursor cursor = dbForUpdate.rawQuery(query,null);
            int playlistId=0;
            if( cursor != null && cursor.moveToFirst() ) {
                playlistId = Integer.parseInt(cursor.getString(0));
                cur.close();
            }

            ContentValues value = new ContentValues();
            value.put("playlistId",playlistId);
            dbForUpdate.update("tbl_fromyoutube_videos",value,"playlistId=?",new String[]{String.valueOf(playlistIdTemb)});
            //dbForUpdate.close();/**closed by Me**/
        }
        db.close();
    }
    public void videoDeleteFromFavs(String videoId,String tblName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tblName, "videoId = ?",new String[]{String.valueOf(videoId)});
        db.close();
    }
    public void deletePlaylist(String playlistName,Context ct) {
        DatabaseForPlaylists dbForQuery = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = dbForQuery.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT id FROM tbl_fromyoutube_playlist where playlistName=?",new String[]{playlistName});
        int playlistId=0;
        if( cur != null && cur.moveToFirst() ) {
            playlistId=Integer.parseInt(cur.getString(0));
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tbl_fromyoutube_playlist", "playlistName = ?",new String[]{String.valueOf(playlistName)});
        db.delete("tbl_fromyoutube_videos", "playlistId = ?",new String[]{String.valueOf(playlistId)});
        db.close();
    }
    public void updateVideoCount(String playlistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update tbl_fromyoutube_playlist set videoCount=(select videoCount from tbl_fromyoutube_playlist where playlistName=?)-1 where playlistName=?",new String[]{playlistName,playlistName});
    }
}
