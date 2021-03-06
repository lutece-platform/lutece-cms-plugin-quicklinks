/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.quicklinks.business;

/**
 *
 * Class entryType
 *
 */
public class EntryType
{
    private int _nId;
    private String _strTitleKey;
    private String _strClassName;
    private String _strTemplateCreate;
    private String _strTemplateModify;

    /**
     * @return the templateCreate
     */
    public String getTemplateCreate( )
    {
        return _strTemplateCreate;
    }

    /**
     * @param strTemplateCreate
     *            the templateCreate to set
     */
    public void setTemplateCreate( String strTemplateCreate )
    {
        this._strTemplateCreate = strTemplateCreate;
    }

    /**
     * @return the templateModify
     */
    public String getTemplateModify( )
    {
        return _strTemplateModify;
    }

    /**
     * @param strTemplateModify
     *            the templateModify to set
     */
    public void setTemplateModify( String strTemplateModify )
    {
        this._strTemplateModify = strTemplateModify;
    }

    /**
     *
     * @return the id of the entry type
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * set the id of the entry type
     * 
     * @param nId
     *            the id of the entry type
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     *
     * @return the I18n key title of the entry type
     */
    public String getTitleI18nKey( )
    {
        return _strTitleKey;
    }

    /**
     * set the I18n key title of the entry type
     * 
     * @param strTitle
     *            the I18n key title of the entry type
     */
    public void setTitleI18nKey( String strTitle )
    {
        _strTitleKey = strTitle;
    }

    /**
     *
     * @return the path for acces to the Classe Entry
     */
    public String getClassName( )
    {
        return _strClassName;
    }

    /**
     * set the path for acces to the Class Entry
     * 
     * @param strClassName
     *            the path for acces to the Class Entry
     */
    public void setClassName( String strClassName )
    {
        _strClassName = strClassName;
    }
}
