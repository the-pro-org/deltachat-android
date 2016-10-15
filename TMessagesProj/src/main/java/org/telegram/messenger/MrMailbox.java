/*******************************************************************************
 *
 *                          Messenger Android Frontend
 *     Copyright (C) 2016 Björn Petersen Software Design and Development
 *                   Contact: r10s@b44t.com, http://b44t.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see http://www.gnu.org/licenses/ .
 *
 *******************************************************************************
 *
 * File:    MrMailbox.java
 * Authors: Björn Petersen
 * Purpose: The Java part of the Java<->C Wrapper, see also mr_wrapper.c
 *
 ******************************************************************************/


// EDIT BY MR
package org.telegram.messenger;


import org.telegram.tgnet.TLRPC;

public class MrMailbox {

    public static long           hMailbox = 0;
    public static long           hCurrChatlist = 0;


    // tools
    public static TLRPC.TL_dialog hChat2dialog(long hChat)
    {
        TLRPC.TL_dialog ret = new TLRPC.TL_dialog();
        ret.id = MrMailbox.MrChatGetId(hChat);
        return ret;
    }

    public static TLRPC.TL_dialog hChatlist2dialog(long hChatlist, int index)
    {
        long hChat = MrMailbox.MrChatlistGetChatByIndex(hChatlist, index);
        TLRPC.TL_dialog dlg = hChat2dialog(hChat);
        MrMailbox.MrChatUnref(hChat);
        return dlg;
    }

    public static TLRPC.Message hMsg2Message(long hMsg)
    {
        TLRPC.Message ret = new TLRPC.TL_message(); // the class derived from TLRPC.Message defines the basic type:
                                                    //  TLRPC.TL_messageService is used to display messages as "You joined the group"
                                                    //  TLRPC.TL_message is a normal message (also photos?)

        ret.from_id = (int)MrMailbox.MrMsgGetFromId(hMsg);
        if( ret.from_id == 0 ) {
            ret.from_id = -1; // from us
        }

        ret.to_id = new TLRPC.TL_peerUser();
        ret.to_id.user_id = -1; // to chat

        ret.message = MrMailbox.MrMsgGetText(hMsg);
        ret.date = (int)MrMsgGetTimestamp(hMsg);

        // MessageObject.contentType - ?
        return ret;
    }

    public static TLRPC.User contactId2user(int id)
    {
        TLRPC.User ret = new TLRPC.User();
        ret.id = id;
        return ret;
    }

    // this function is called from within the C-wrapper
    public static long MrCallback(int event, long data1, long data2)
    {
        return 0;
    }

    // MrMailbox objects
    public native static long    MrMailboxNew               (); // returns hMailbox which must be unref'd after usage (Names as mrmailbox_new don't work due to the additional underscore)
    public native static int     MrMailboxOpen              (long hMailbox, String dbfile, String blobdir);
    public native static void    MrMailboxClose             (long hMailbox);
    public native static int     MrMailboxConfigure         (long hMailbox);
    public native static int     MrMailboxIsConfigured      (long hMailbox);
    public native static int     MrMailboxConnect           (long hMailbox);
    public native static void    MrMailboxDisconnect        (long hMailbox);
    public native static int     MrMailboxFetch             (long hMailbox);

    public native static long    MrMailboxGetContactById    (long hMailbox);// returns hContact which must be unref'd after usage

    public native static long    MrMailboxGetChatlist       (long hMailbox); // returns hChatlist which must be unref'd after usage
    public native static long    MrMailboxGetChatById       (long hMailbox, int id); // return hChat which must be unref'd after usage

    public native static int     MrMailboxSetConfig         (long hMailbox, String key, String value); // value may be NULL
    public native static String  MrMailboxGetConfig         (long hMailbox, String key, String def); // def may be NULL, returns empty string as NULL

    public native static String  MrMailboxGetInfo           (long hMailbox);
    public native static String  MrMailboxExecute           (long hMailbox, String cmd);

    // MrChatlist objects
    public native static void    MrChatlistUnref            (long hChatlist);
    public native static int     MrChatlistGetCnt           (long hChatlist);
    public native static int     MrChatlistGetChatByIndex   (long hChatlist, int index); // returns hChat which must be unref'd after usage

    // MrChat objects
    public native static void    MrChatUnref                (long hChat);
    public native static int     MrChatGetId                (long hChat);
    public native static int     MrChatGetType              (long hChat);
    public native static String  MrChatGetName              (long hChat);
    public native static String  MrChatGetSubtitle          (long hChat);
    public native static int     MrChatGetUnreadCount       (long hChat);
    public native static long    MrChatGetSummary           (long hChat); // returns hPoortext
    public native static long    MrChatGetMsglist           (long hChat, int offset, int amount); // returns hMsglist

    // MrMsglist objects
    public native static void    MrMsglistUnref             (long hMsglist);
    public native static int     MrMsglistGetCnt            (long hMsglist);
    public native static int     MrMsglistGetMsgByIndex     (long hMsglist, int index); // returns hMsg which must be unref'd after usage

    // MrMsg objects
    public native static void    MrMsgUnref                 (long hMsg);
    public native static String  MrMsgGetText               (long hMsg);
    public native static long    MrMsgGetTimestamp          (long hMsg);
    public native static int     MrMsgGetType               (long hMsg);
    public native static int     MrMsgGetState              (long hMsg);
    public native static int     MrMsgGetFromId             (long hMsg); // returns user ID, 0=self

    // MrPoortext objects
    public native static void    MrPoortextUnref            (long hPoortext);
    public native static String  MrPoortextGetTitle         (long hPoortext);
    public native static int     MrPoortextGetTitleMeaning  (long hPoortext);
    public native static String  MrPoortextGetText          (long hPoortext);
    public native static long    MrPoortextGetTimestamp     (long hPoortext);
    public native static int     MrPoortextGetState         (long hPoortext);

    // Tools
    public native static void    MrStockAddStr              (int id, String str);
    public native static String  MrGetVersionStr            ();

    public final static int      MR_CHAT_UNDEFINED          = 0;
    public final static int      MR_CHAT_NORMAL             = 100;
    public final static int      MR_CHAT_ENCRYPTED          = 110;
    public final static int      MR_CHAT_GROUP              = 120;
    public final static int      MR_CHAT_FEED               = 130;

    public final static int      MR_STATE_UNDEFINED         = 0;
    public final static int      MR_IN_UNREAD               = 1;
    public final static int      MR_IN_READ                 = 3;
    public final static int      MR_OUT_PENDING             = 5;
    public final static int      MR_OUT_ERROR               = 6;
    public final static int      MR_OUT_DELIVERED           = 7;
    public final static int      MR_OUT_READ                = 9;

    public final static int      MR_TITLE_NORMAL            = 0;
    public final static int      MR_TITLE_DRAFT             = 1;
    public final static int      MR_TITLE_USERNAME          = 2;

}
// /EDIT BY MR